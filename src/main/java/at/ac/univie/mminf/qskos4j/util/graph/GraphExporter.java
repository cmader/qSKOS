package at.ac.univie.mminf.qskos4j.util.graph;

import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
		int i = 0;
		Iterator<Set<URI>> it = vertexSubSets.iterator();
		while (it.hasNext()) {
			Graph<URI, NamedEdge> componentGraph = getGraphForComponent(it.next());
			new DOTExporter<URI, NamedEdge>(
				new IntegerNameProvider<URI>(),
				new URIVertexNameProvider(),
				new StringEdgeNameProvider<NamedEdge>()
			).export(outputWriters[i], (DirectedGraph<URI, NamedEdge>) componentGraph);
			i++;
		}

	}
	
	private Graph<URI, NamedEdge> getGraphForComponent(Collection<URI> component)
	{
		return new DirectedSubgraph<URI, NamedEdge>(graph, new HashSet<URI>(component), null);
	}
	
	private class URIVertexNameProvider implements VertexNameProvider<URI>
	{
		@Override
		public String getVertexName(URI vertex) {
			return vertex.getLocalName();
		}	
	}
}
