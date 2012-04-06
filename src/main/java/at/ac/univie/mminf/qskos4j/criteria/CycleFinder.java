package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.result.custom.HierarchyCycleResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies hierarchical inconsistencies in the repository passed to the constructor 
 * @author christian
 */
public class CycleFinder extends Criterion {
	
	private enum HierarchyStyle {BROADER, NARROWER}
	
	private final String skosBroaderProperties = "skos:broader, skos:broaderTransitive, skos:broadMatch";
	private final String skosNarrowerProperties = "skos:narrower, skos:narrowerTransitive, skos:narrowMatch";	

	private DirectedGraph<Resource, NamedEdge> hierarchyGraph;
	private List<Set<Resource>> cycleContainingComponents;
	
	public CycleFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public HierarchyCycleResult findCycleContainingComponents() throws OpenRDFException 
	{
		TupleQueryResult broaderResult = findTriples(HierarchyStyle.BROADER);
		TupleQueryResult narrowerResult = findTriples(HierarchyStyle.NARROWER);
		createGraph(broaderResult, narrowerResult);
		
		Set<Resource> nodesInCycles = new CycleDetector<Resource, NamedEdge>(hierarchyGraph).findCycles();
		cycleContainingComponents = trackNodesInCycles(nodesInCycles);
		return new HierarchyCycleResult(cycleContainingComponents, hierarchyGraph);
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
		return vocabRepository.query(query);
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
		hierarchyGraph = new DirectedMultigraph<Resource, NamedEdge>(NamedEdge.class);
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
		Resource resourceNode = (Resource) resource;		
		hierarchyGraph.addVertex(resourceNode);
		
		Resource otherNode = (Resource) otherResource;
		hierarchyGraph.addVertex(otherNode);

		if (invertEdges) {
			hierarchyGraph.addEdge(otherNode, resourceNode, new NamedEdge());
		}
		else {
			hierarchyGraph.addEdge(resourceNode, otherNode, new NamedEdge());
		}
	}
		
	private List<Set<Resource>> trackNodesInCycles(Set<Resource> nodesInCycles) 
	{
		List<Set<Resource>> ret = new ArrayList<Set<Resource>>();
		List<Set<Resource>> stronglyConnectedSets =
			new StrongConnectivityInspector<Resource, NamedEdge>(hierarchyGraph).stronglyConnectedSets();
		
		for (Resource node : nodesInCycles) {
			for (Set<Resource> stronglyConnectedSet : stronglyConnectedSets) {
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
