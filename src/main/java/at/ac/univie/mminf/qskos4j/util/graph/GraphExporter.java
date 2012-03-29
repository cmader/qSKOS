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
import org.openrdf.model.Resource;

public class GraphExporter {

	private DirectedGraph<Resource, NamedEdge> graph;
	
	public GraphExporter(DirectedGraph<Resource, NamedEdge> graph) {
		this.graph = graph;
	}
	
	public void exportSubGraph(List<Set<Resource>> vertexSubSets, Writer[] outputWriters) {
		int i = 0;
		Iterator<Set<Resource>> it = vertexSubSets.iterator();
		while (it.hasNext()) {
			Graph<Resource, NamedEdge> componentGraph = getGraphForComponent(it.next());
			new DOTExporter<Resource, NamedEdge>(
				new IntegerNameProvider<Resource>(),
				new URIVertexNameProvider(),
				new StringEdgeNameProvider<NamedEdge>()
			).export(outputWriters[i], (DirectedGraph<Resource, NamedEdge>) componentGraph);
			i++;
		}

	}
	
	private Graph<Resource, NamedEdge> getGraphForComponent(Collection<Resource> component)
	{
		return new DirectedSubgraph<Resource, NamedEdge>(graph, new HashSet<Resource>(component), null);
	}
	
	private class URIVertexNameProvider implements VertexNameProvider<Resource>
	{
		@Override
		public String getVertexName(Resource vertex) {
			return vertex.stringValue();
		}	
	}
}
