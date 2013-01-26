package at.ac.univie.mminf.qskos4j.util.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DirectedSubgraph;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GraphExporter {

	private DirectedGraph<Value, NamedEdge> graph;
	
	public GraphExporter(DirectedGraph<Value, NamedEdge> graph) {
		this.graph = graph;
	}
	
	public Collection<String> exportSubGraphs(Collection<Set<Value>> vertexSubSets) {
		Set<String> dotGraphs = new HashSet<String>();
		
		Iterator<Set<Value>> it = vertexSubSets.iterator();
		while (it.hasNext()) {
			Graph<Value, NamedEdge> componentGraph = getGraphForComponent(it.next());
			dotGraphs.add(exportGraph(componentGraph));
		}
		
		return dotGraphs;
	}
	
	private Graph<Value, NamedEdge> getGraphForComponent(Collection<Value> component)
	{
		return new DirectedSubgraph<Value, NamedEdge>(graph, new HashSet<Value>(component), null);
	}
	
	private String exportGraph(Graph<Value, NamedEdge> componentGraph) {
		StringWriter outputWriter = new StringWriter();
		
		new DOTExporter<Value, NamedEdge>(
			new IntegerNameProvider<Value>(),
			new URIVertexNameProvider(),
			new StringEdgeNameProvider<NamedEdge>()
		).export(outputWriter, componentGraph);
		
		return outputWriter.toString();
	}
			
	private class URIVertexNameProvider implements VertexNameProvider<Value>
	{
		@Override
		public String getVertexName(Value vertex) {
			return vertex.stringValue();
		}	
	}
}
