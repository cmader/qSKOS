package at.ac.univie.mminf.qskos4j.issues;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.custom.HierarchyCycleResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;

/**
 * Identifies hierarchical inconsistencies in the repository passed to the constructor 
 * @author christian
 */
public class CycleFinder extends Issue {
	
	private DirectedGraph<Resource, NamedEdge> hierarchyGraph;
	private List<Set<Resource>> cycleContainingComponents;
	
	public CycleFinder(DirectedGraph<Resource, NamedEdge> hierarchyGraph) 
	{
		this.hierarchyGraph = hierarchyGraph;
	}
	
	public HierarchyCycleResult findCycleContainingComponents() throws OpenRDFException 
	{
		Set<Resource> nodesInCycles = new CycleDetector<Resource, NamedEdge>(hierarchyGraph).findCycles();
		cycleContainingComponents = trackNodesInCycles(nodesInCycles);
		return new HierarchyCycleResult(cycleContainingComponents, hierarchyGraph);
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
