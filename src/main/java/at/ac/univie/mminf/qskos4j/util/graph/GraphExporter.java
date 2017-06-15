package at.ac.univie.mminf.qskos4j.util.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DirectedSubgraph;
import org.eclipse.rdf4j.model.Resource;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GraphExporter {

	private DirectedGraph<Resource, NamedEdge> graph;
	
	public GraphExporter(DirectedGraph<Resource, NamedEdge> graph) {
		this.graph = graph;
	}
	
	public Collection<String> exportDotGraphs(Collection<Collection<Resource>> vertexSubSets) {
		Set<String> dotGraphs = new HashSet<String>();

        for (Collection<Resource> component : vertexSubSets) {
			Graph<Resource, NamedEdge> componentGraph = getGraphForComponent(component);
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
		).export(outputWriter, componentGraph);
		
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
