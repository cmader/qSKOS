package at.ac.univie.mminf.qskos4j.result.custom;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;

public class WeaklyConnectedComponentsResult extends Result<DirectedGraph<URI, NamedEdge>> {

	private List<Set<URI>> connectedSets;
	
	public WeaklyConnectedComponentsResult(DirectedGraph<URI, NamedEdge> data) {
		super(data);
		
		connectedSets = new ConnectivityInspector<URI, NamedEdge>(getData()).connectedSets();
	}

	@Override
	public String getShortReport() {
		return "count: " +connectedSets.size();
	}

	@Override
	public String getExtensiveReport() {
		String detailedReport = "";
		long compCount = 1;
		
		for (Set<URI> component : connectedSets) {
			detailedReport += "component " +compCount+ ": " +component.toString()+ "\n";
			compCount++;
		}
		
		return detailedReport;
	}

	public void exportComponentsAsDOT(Writer[] writers) {
		new GraphExporter(getData()).exportSubGraph(connectedSets, writers);
	}
	
	public List<Set<URI>> getConnectedSets() {
		return connectedSets;
	}
}
