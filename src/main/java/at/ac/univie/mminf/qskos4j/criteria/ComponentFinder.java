package at.ac.univie.mminf.qskos4j.criteria;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.result.custom.WeaklyConnectedComponentsResult;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies all weakly connected components in the repository passed to the constructor
 * @author christian
 */
public class ComponentFinder extends Criterion {

	private DirectedGraph<URI, NamedEdge> graph;
	private List<Set<URI>> connectedSets;
	
	public ComponentFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public WeaklyConnectedComponentsResult findComponents() throws OpenRDFException 
	{
		if (graph == null) {
			TupleQueryResult result = findTriples();
			createGraph(result);
		}
		connectedSets = new ConnectivityInspector<URI, NamedEdge>(graph).connectedSets(); 
		return new WeaklyConnectedComponentsResult(connectedSets);
	}
	
	public void exportComponents(Writer[] writers) throws OpenRDFException
	{	
		if (connectedSets == null) {
			findComponents();
		}
		
		new GraphExporter(graph).exportSubGraph(connectedSets, writers);
	}
		
	private TupleQueryResult findTriples() 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		String query = createNodeQuery();
		return vocabRepository.query(query);
	}
	
	/**
	 * Creates a SPARQL query to retrieve the information needed for component finding.
	 * Nodes are all instances of (subclasses of) skos classes. Edges in the graph are
	 * all skos properties relating the nodes to each other. 
	 * @return
	 */
	private String createNodeQuery() {
		String query = SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +" "+ SparqlPrefix.OWL +
			"SELECT ?skosResource ?skosRelation ?otherResource "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"FROM NAMED <" +vocabRepository.SKOS_GRAPH_URL+ "> "+
			
			"WHERE {" +
				"{" +
					"?skosResource rdf:type+/rdfs:subClassOf* ?skosClass . "+
					"GRAPH <"+vocabRepository.SKOS_GRAPH_URL+"> "+
					"{" +
						"?skosClass rdf:type owl:Class ." +
					"}" +
				"} " +
				"UNION "+
				"{" +
					"?skosResource ?relation ?otherResource . "+
					"?relation rdfs:subPropertyOf* ?skosRelation . "+
					"GRAPH <"+vocabRepository.SKOS_GRAPH_URL+"> " +
					"{" +
						"?skosRelation rdfs:isDefinedBy <http://www.w3.org/2004/02/skos/core> ." +
					"} "+
					"FILTER isIRI(?otherResource)" +
				"} "+
			"}";
				
		return query;
	}
	
	private void createGraph(TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		graph = new DirectedMultigraph<URI, NamedEdge>(NamedEdge.class);
		
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			
			addNodesToGraph(
				bindingSet.getValue("skosResource"),
				bindingSet.getValue("otherResource"),
				bindingSet.getValue("skosRelation"));
		}
	}
	
	private void addNodesToGraph(
		Value skosResource, 
		Value otherResource,
		Value skosRelation)
	{
		URI node1 = new URIImpl(skosResource.stringValue());
		graph.addVertex(node1);
		
		if (otherResource != null) {
			URI node2 = new URIImpl(otherResource.stringValue());
			graph.addVertex(node2);
			
			URI relation = new URIImpl(skosRelation.stringValue());
			graph.addEdge(node1, node2, new NamedEdge(relation.getLocalName()));
		}
	}
		
}
