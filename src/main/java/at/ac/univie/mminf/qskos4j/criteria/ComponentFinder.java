package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.Collection;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.custom.WeaklyConnectedComponentsResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies all weakly connected components in the repository passed to the constructor
 * @author christian
 */
public class ComponentFinder extends Criterion {

	private DirectedGraph<URI, NamedEdge> graph;
	
	public ComponentFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public WeaklyConnectedComponentsResult findComponents(Collection<URI> allConcepts) 
		throws OpenRDFException 
	{
		if (graph == null) {
			createGraph(allConcepts);
		}
		
		return new WeaklyConnectedComponentsResult(graph);
	}
	
	private void createGraph(Collection<URI> allConcepts) 
		throws OpenRDFException
	{
		graph = new DirectedMultigraph<URI, NamedEdge>(NamedEdge.class);
		
		for (URI concept : allConcepts) {
			Collection<Relation> relations = findRelations(concept);
			
			for (Relation relation : relations) {
				addNodesToGraph(
					relation.sourceConcept,
					relation.targetConcept,
					relation.property);
			}
		}
	}
	
	private Collection<Relation> findRelations(URI concept) 
		throws OpenRDFException
	{
		Collection<Relation> allRelations = new ArrayList<Relation>();
		
		TupleQueryResult result = vocabRepository.query(createConnectionsQuery(concept));
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value otherConcept = bindingSet.getValue("otherConcept");
			Value semanticRelation = bindingSet.getValue("semanticRelation");
			
			if (otherConcept != null && semanticRelation != null) {
				allRelations.add(new Relation(concept, (URI) otherConcept, (URI) semanticRelation));
			}
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
		URI skosResource, 
		URI otherResource,
		URI skosRelation)
	{
		graph.addVertex(skosResource);
		
		if (otherResource != null) {
			graph.addVertex(otherResource);
			graph.addEdge(skosResource, otherResource, new NamedEdge(skosRelation.getLocalName()));
		}
	}
	
	private class Relation {
		private URI sourceConcept, targetConcept, property;
		
		private Relation(URI sourceConcept, URI targetConcept, URI property) {
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
