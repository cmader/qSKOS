package at.ac.univie.mminf.qskos4j.util.graph;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
	
	public Collection<String> exportSubGraphs(Collection<Set<Resource>> vertexSubSets) {
		Set<String> dotGraphs = new HashSet<String>();
		
		Iterator<Set<Resource>> it = vertexSubSets.iterator();
		while (it.hasNext()) {
			Graph<Resource, NamedEdge> componentGraph = getGraphForComponent(it.next());
			dotGraphs.add(exportGraph(componentGraph));
		}
		
		return dotGraphs;
	}
	
	private Graph<Resource, NamedEdge> getGraphForComponent(Collection<Resource> component)
	{
		return new DirectedSubgraph<Resource, NamedEdge>(graph, new HashSet<Resource>(component), null);
	}
	
	private String exportGraph(Graph<Resource, NamedEdge> componentGraph) {
		StringWriter outputWriter = new StringWriter();
		
		new DOTExporter<Resource, NamedEdge>(
			new IntegerNameProvider<Resource>(),
			new URIVertexNameProvider(),
			new StringEdgeNameProvider<NamedEdge>()
		).export(outputWriter, (DirectedGraph<Resource, NamedEdge>) componentGraph);
		
		return outputWriter.toString();
	}
			
	private class URIVertexNameProvider implements VertexNameProvider<Resource>
	{
		@Override
		public String getVertexName(Resource vertex) {
			return vertex.stringValue();
		}	
	}
}
