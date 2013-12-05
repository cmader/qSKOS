package at.ac.univie.mminf.qskos4j.issues.clusters;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
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
public class DisconnectedConceptClusters extends Issue<ClustersResult> {

    private final Logger logger = LoggerFactory.getLogger(DisconnectedConceptClusters.class);

    private DirectedGraph<Resource, NamedEdge> graph;
    private InvolvedConcepts involvedConcepts;

    public DisconnectedConceptClusters(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts,
            "dcc",
            "Disconnected Concept Clusters",
            "Finds sets of concepts that are isolated from the rest of the vocabulary",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#disconnected-concept-clusters")
        );
        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected ClustersResult invoke() throws OpenRDFException {
        createGraph();

        Collection<Collection<Resource>> connectedSets = new ArrayList<Collection<Resource>>();
        connectedSets.addAll(new ConnectivityInspector<Resource, NamedEdge>(graph).connectedSets());

        return new ClustersResult(connectedSets, graph);
    }

    private void createGraph() throws OpenRDFException
    {
        graph = new DirectedMultigraph<Resource, NamedEdge>(NamedEdge.class);

        Iterator<Resource> conceptIt = new MonitoredIterator<Resource>(involvedConcepts.getResult().getData(), progressMonitor);
        while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
            Collection<Relation> relations = findRelations(concept);

            for (Relation relation : relations) {
                addNodesToGraph(
                        relation.sourceConcept,
                        relation.targetConcept,
                        relation.property);
            }
        }
    }

    private Collection<Relation> findRelations(Resource concept)
    {
        Collection<Relation> allRelations = new ArrayList<Relation>();

        try {
            TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createConnectionsQuery(concept));
            TupleQueryResult result = query.evaluate();

            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Resource otherConcept = (Resource) bindingSet.getValue("otherConcept");
                URI semanticRelation = (URI) bindingSet.getValue("semanticRelation");

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

    private String createConnectionsQuery(Value concept) throws OpenRDFException {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS+
            "SELECT DISTINCT ?otherConcept ?semanticRelation WHERE " +
            "{" +
                "<" +concept.stringValue()+ "> ?semanticRelation ?otherConcept . " +
                "?semanticRelation rdfs:subPropertyOf skos:semanticRelation" +
            "}";
    }

    private void addNodesToGraph(
            Resource skosResource,
            Resource otherResource,
            Resource skosRelation)
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
        private Resource sourceConcept, targetConcept, property;

        private Relation(Resource sourceConcept, Resource targetConcept, Resource property) {
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
