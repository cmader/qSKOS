package at.ac.univie.mminf.qskos4j.util.graph;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DirectedSubgraph;
import org.openrdf.model.URI;

public class GraphExporter {

	private DirectedGraph<URI, NamedEdge> graph;
	
	public GraphExporter(DirectedGraph<URI, NamedEdge> graph) {
		this.graph = graph;
	}
	
	public void exportSubGraph(List<Set<URI>> vertexSubSets, Writer[] outputWriters) {
		for (int i = 0; i < vertexSubSets.size(); i++) {
			Graph<URI, NamedEdge> componentGraph = getGraphForComponent(vertexSubSets.get(i));
			new DOTExporter<URI, NamedEdge>(
				new IntegerNameProvider<URI>(),
				new URIVertexNameProvider(),
				new StringEdgeNameProvider<NamedEdge>()
			).export(outputWriters[i], (DirectedGraph<URI, NamedEdge>) componentGraph);
		}

	}
	
	private Graph<URI, NamedEdge> getGraphForComponent(Set<URI> component)
	{
		return new DirectedSubgraph<URI, NamedEdge>(graph, component, null);
	}
	
	private class URIVertexNameProvider implements VertexNameProvider<URI>
	{
		@Override
		public String getVertexName(URI vertex) {
			return vertex.getLocalName();
		}	
	}
}
