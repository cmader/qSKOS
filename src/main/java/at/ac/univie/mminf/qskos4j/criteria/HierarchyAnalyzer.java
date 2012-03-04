package at.ac.univie.mminf.qskos4j.criteria;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
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

import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies hierarchical inconsistencies in the repository passed to the constructor 
 * @author christian
 */
public class HierarchyAnalyzer extends Criterion {
	
	private enum HierarchyStyle {BROADER, NARROWER}
	
	private final String skosBroaderProperties = "skos:broader, skos:broaderTransitive, skos:broadMatch";
	private final String skosNarrowerProperties = "skos:narrower, skos:narrowerTransitive, skos:narrowMatch";	

	private DirectedGraph<URI, NamedEdge> hierarchyGraph;
	private List<Set<URI>> cycleContainingComponents;
	
	public HierarchyAnalyzer(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public List<Set<URI>> findCycleContainingComponents() throws OpenRDFException 
	{
		if (hierarchyGraph == null) {
			TupleQueryResult broaderResult = findTriples(HierarchyStyle.BROADER);
			TupleQueryResult narrowerResult = findTriples(HierarchyStyle.NARROWER);
			createGraph(broaderResult, narrowerResult);
		}
		
		Set<URI> nodesInCycles = new CycleDetector<URI, NamedEdge>(hierarchyGraph).findCycles();
		cycleContainingComponents = trackNodesInCycles(nodesInCycles);
		return cycleContainingComponents;
	}
	
	public void exportCycleContainingComponents(Writer[] writers) throws OpenRDFException 
	{
		if (cycleContainingComponents == null) {
			findCycleContainingComponents();
		}
		
		new GraphExporter(hierarchyGraph).exportSubGraph(cycleContainingComponents, writers);
	}
		
	private TupleQueryResult findTriples(HierarchyStyle hierarchyStyle)
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		String skosHierarchyProperties = null;
		switch (hierarchyStyle) {
		case BROADER:
			skosHierarchyProperties = skosBroaderProperties;
			break;
		case NARROWER:
			skosHierarchyProperties = skosNarrowerProperties;
		}
		
		String query = createHierarchicalGraphQuery(skosHierarchyProperties);
		return queryRepository(query);
	}
	
	private String createHierarchicalGraphQuery(String skosHierarchyProperties) {
		String query = SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource ?otherResource "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"FROM <" +vocabRepository.SKOS_GRAPH_URL+ "> "+

			"WHERE {?resource ?hierarchyRelation ?otherResource . "+
				"?hierarchyRelation rdfs:subPropertyOf* ?skosHierarchyRelation . "+
				"FILTER (?skosHierarchyRelation IN (" +skosHierarchyProperties+ "))}";
				
		return query;
	}
	
	private void createGraph(TupleQueryResult broaderResult, TupleQueryResult narrowerResult) 
		throws QueryEvaluationException 
	{
		hierarchyGraph = new DirectedMultigraph<URI, NamedEdge>(NamedEdge.class);
		addResultsToGraph(broaderResult, false);
		addResultsToGraph(narrowerResult, true);
	}
	
	private void addResultsToGraph(TupleQueryResult result, boolean invertEdges) 
		throws QueryEvaluationException
	{	
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			
			addToGraph(
				bindingSet.getValue("resource"), 
				bindingSet.getValue("otherResource"), 
				invertEdges);
		}
	}
	
	private void addToGraph(
		Value resource, 
		Value otherResource,
		boolean invertEdges) 
	{
		URI resourceNode = new URIImpl(resource.stringValue());		
		hierarchyGraph.addVertex(resourceNode);
		
		URI otherNode = new URIImpl(otherResource.stringValue());
		hierarchyGraph.addVertex(otherNode);

		if (invertEdges) {
			hierarchyGraph.addEdge(otherNode, resourceNode, new NamedEdge());
		}
		else {
			hierarchyGraph.addEdge(resourceNode, otherNode, new NamedEdge());
		}
	}
		
	private List<Set<URI>> trackNodesInCycles(Set<URI> nodesInCycles) 
	{
		List<Set<URI>> ret = new ArrayList<Set<URI>>();
		List<Set<URI>> stronglyConnectedSets =
			new StrongConnectivityInspector<URI, NamedEdge>(hierarchyGraph).stronglyConnectedSets();
		
		for (URI node : nodesInCycles) {
			for (Set<URI> stronglyConnectedSet : stronglyConnectedSets) {
				if (stronglyConnectedSet.contains(node)) {
					if (!ret.contains(stronglyConnectedSet)) {
						ret.add(stronglyConnectedSet);
					}
				}
			}
		}
				
		return ret;
	}
	
}
