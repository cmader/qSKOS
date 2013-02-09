package at.ac.univie.mminf.qskos4j.issues.clusters;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:43
 *
 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Disconnected_Concept_Clusters">
 * Disconnected Concept Clusters</a>.
 */
public class DisconnectedConceptClusters extends Issue<ClustersReport> {

    private final Logger logger = LoggerFactory.getLogger(DisconnectedConceptClusters.class);

    private DirectedGraph<Value, NamedEdge> graph;
    private InvolvedConcepts involvedConcepts;

    public DisconnectedConceptClusters(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts.getVocabRepository(),
              "dcc",
              "Disconnected Concept Clusters",
              "Finds sets of concepts that are isolated from the rest of the vocabulary",
              IssueType.ANALYTICAL
        );
        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected ClustersReport invoke() throws OpenRDFException {
        createGraph();

        return new ClustersReport(
                new ConnectivityInspector<Value, NamedEdge>(graph).connectedSets(),
                graph);
    }

    private void createGraph()
            throws OpenRDFException
    {
        graph = new DirectedMultigraph<Value, NamedEdge>(NamedEdge.class);

        Iterator<Value> conceptIt = new MonitoredIterator<Value>(involvedConcepts.getReport().getData(), progressMonitor);
        while (conceptIt.hasNext()) {
            Collection<Relation> relations = findRelations(conceptIt.next());

            for (Relation relation : relations) {
                addNodesToGraph(
                        relation.sourceConcept,
                        relation.targetConcept,
                        relation.property);
            }
        }
    }

    private Collection<Relation> findRelations(Value concept)
    {
        Collection<Relation> allRelations = new ArrayList<Relation>();

        try {
            TupleQueryResult result = vocabRepository.query(createConnectionsQuery(concept));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value otherConcept = bindingSet.getValue("otherConcept");
                Value semanticRelation = bindingSet.getValue("semanticRelation");

                if (otherConcept != null && semanticRelation != null) {
                    allRelations.add(new Relation(concept, otherConcept, semanticRelation));
                }
            }
        }
        catch (OpenRDFException e) {
            logger.error("Error finding relations of concept '" +concept+ "'");
        }

        return allRelations;
    }

    private String createConnectionsQuery(Value concept) {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS+
                "SELECT DISTINCT ?otherConcept ?semanticRelation WHERE " +
                "{" +
                "<" +concept.stringValue()+ "> ?semanticRelation ?otherConcept . ?semanticRelation rdfs:subPropertyOf+ skos:semanticRelation" +
                "}";
    }

    private void addNodesToGraph(
            Value skosResource,
            Value otherResource,
            Value skosRelation)
    {
        graph.addVertex(skosResource);

        if (otherResource != null) {
            graph.addVertex(otherResource);
            graph.addEdge(skosResource, otherResource, new NamedEdge(extractFragmentString(skosRelation)));
        }
    }

    private String extractFragmentString(Value skosRelation) {
        int hashIndex = skosRelation.stringValue().indexOf("#");

        if (hashIndex != -1) {
            return skosRelation.stringValue().substring(hashIndex + 1);
        }

        return skosRelation.stringValue();
    }

    private class Relation {
        private Value sourceConcept, targetConcept, property;

        private Relation(Value sourceConcept, Value targetConcept, Value property) {
            this.sourceConcept = sourceConcept;
            this.targetConcept = targetConcept;
            this.property = property;
        }

        @Override
        public String toString() {
            return sourceConcept.stringValue() +" -- "+ property +" --> "+ targetConcept;
        }
    }

}
