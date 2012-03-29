package at.ac.univie.mminf.qskos4j.result.custom;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;

public class WeaklyConnectedComponentsResult extends Result<DirectedGraph<Resource, NamedEdge>> {

	private List<Set<Resource>> connectedSets;
	
	public WeaklyConnectedComponentsResult(DirectedGraph<Resource, NamedEdge> data) {
		super(data);
		
		connectedSets = new ConnectivityInspector<Resource, NamedEdge>(getData()).connectedSets();
	}

	@Override
	public String getShortReport() {
		return "count: " +connectedSets.size();
	}

	@Override
	public String getExtensiveReport() {
		String detailedReport = "";
		long compCount = 1;
		
		for (Set<Resource> component : connectedSets) {
			detailedReport += "component " +compCount+ ", size: " +component.size()+ "\n" +component.toString()+ "\n";
			compCount++;
		}
		
		return detailedReport;
	}

	public void exportComponentsAsDOT(Writer[] writers) {
		new GraphExporter(getData()).exportSubGraph(connectedSets, writers);
	}
	
	public List<Set<Resource>> getConnectedSets() {
		return connectedSets;
	}
}
