package at.ac.univie.mminf.qskos4j.issues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.custom.WeaklyConnectedComponentsResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Identifies all weakly connected components in the repository passed to the constructor
 * @author christian
 */
public class ComponentFinder extends Issue {

    private final Logger logger = LoggerFactory.getLogger(ComponentFinder.class);

	private DirectedGraph<Resource, NamedEdge> graph;
	
	public ComponentFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public WeaklyConnectedComponentsResult findComponents(Collection<URI> allConcepts) 
		throws OpenRDFException 
	{
		if (graph == null) {
			createGraph(allConcepts);
		}
		
		return new WeaklyConnectedComponentsResult(
			new ConnectivityInspector<Resource, NamedEdge>(graph).connectedSets(),
			graph);
	}
	
	private void createGraph(Collection<URI> allConcepts) 
		throws OpenRDFException
	{
		graph = new DirectedMultigraph<Resource, NamedEdge>(NamedEdge.class);
		
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(allConcepts, progressMonitor);
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
	
	private Collection<Relation> findRelations(URI concept) 
	{
		Collection<Relation> allRelations = new ArrayList<Relation>();

        try {
            TupleQueryResult result = vocabRepository.query(createConnectionsQuery(concept));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value otherConcept = bindingSet.getValue("otherConcept");
                Value semanticRelation = bindingSet.getValue("semanticRelation");

                if (otherConcept != null && semanticRelation != null) {
                    allRelations.add(new Relation(concept, (Resource) otherConcept, (Resource) semanticRelation));
                }
            }
        }
        catch (OpenRDFException e) {
            logger.error("Error finding relations of concept '" +concept+ "'");
        }
		
		return allRelations;
	}
	
	private String createConnectionsQuery(URI concept) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS+
			"SELECT DISTINCT ?otherConcept ?semanticRelation WHERE " +
			"{" +
			"<" +concept.stringValue()+ "> ?semanticRelation ?otherConcept . ?semanticRelation rdfs:subPropertyOf+ skos:semanticRelation" +
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
	
	private String extractFragmentString(Resource skosRelation) {
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
