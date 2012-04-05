package at.ac.univie.mminf.qskos4j.util.graph;

import java.io.Writer;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringEdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.openrdf.model.Resource;

public class GraphWriter {

	private DirectedGraph<Resource, NamedEdge> graph;
	
	public GraphWriter(DirectedGraph<Resource, NamedEdge> graph) {
		this.graph = graph;
	}
	
	public void write(Writer outputWriter) {
		new DOTExporter<Resource, NamedEdge>(
			new IntegerNameProvider<Resource>(),
			new URIVertexNameProvider(),
			new StringEdgeNameProvider<NamedEdge>()).export(outputWriter, graph);
	}
			
	private class URIVertexNameProvider implements VertexNameProvider<Resource>
	{
		@Override
		public String getVertexName(Resource vertex) {
			return vertex.stringValue();
		}	
	}
}
