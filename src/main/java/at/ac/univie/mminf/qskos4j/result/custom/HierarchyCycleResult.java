package at.ac.univie.mminf.qskos4j.result.custom;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.Set;

public class HierarchyCycleResult extends CollectionResult<Set<Resource>> {

	private DirectedGraph<Resource, NamedEdge> graph;
	
	public HierarchyCycleResult(Collection<Set<Resource>> data, DirectedGraph<Resource, NamedEdge> graph) {
		super(data);
		this.graph = graph;
	}

	@Override
	public Collection<String> getAsDOT() {
		return new GraphExporter(graph).exportSubGraphs(getData());
	}
	
}
