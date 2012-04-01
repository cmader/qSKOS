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
		return generateReport(false);
	}

	@Override
	public String getExtensiveReport() {
		return generateReport(true);
	}
	
	private String generateReport(boolean extensive) {
		String report = "";
		long compCount = 1;
		
		report += "count: " +connectedSets.size() +"\n";
		for (Set<Resource> component : connectedSets) {
			report += "component " +compCount+ ", size: " +component.size()+ "\n";
			if (extensive) {
				report += component.toString()+ "\n";
			}
			compCount++;
		}
		
		return report;
	}

	public void exportComponentsAsDOT(Writer[] writers) {
		new GraphExporter(getData()).exportSubGraph(connectedSets, writers);
	}
	
	public List<Set<Resource>> getConnectedSets() {
		return connectedSets;
	}
}
