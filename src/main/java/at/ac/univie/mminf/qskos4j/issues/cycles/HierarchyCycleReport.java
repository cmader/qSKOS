package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.result.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Set;

public class HierarchyCycleReport extends CollectionReport<Set<Value>> {

	private DirectedGraph<Value, NamedEdge> graph;
	
	public HierarchyCycleReport(Collection<Set<Value>> data, DirectedGraph<Value, NamedEdge> graph) {
		super(data);
		this.graph = graph;
	}

	@Override
	public Collection<String> getAsDOT() {
		return new GraphExporter(graph).exportSubGraphs(getData());
	}
	
}
