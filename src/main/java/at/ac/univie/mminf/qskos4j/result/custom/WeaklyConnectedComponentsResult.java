package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;

public class WeaklyConnectedComponentsResult extends CollectionResult<Set<Resource>>
{
	public WeaklyConnectedComponentsResult(Collection<Set<Resource>> data, DirectedGraph<Resource, NamedEdge> graph) {
		super(data, graph);
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
		
		report += "count: " +getData().size() +"\n";
		for (Set<Resource> component : getData()) {
			report += "component " +compCount+ ", size: " +component.size()+ "\n";
			if (extensive) {
				report += component.toString()+ "\n";
			}
			compCount++;
		}
		
		return report;
	}

}
