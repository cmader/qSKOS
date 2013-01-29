package at.ac.univie.mminf.qskos4j.issues.clusters;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Set;

public class ClustersResult extends CollectionResult<Set<Value>>
{
	private DirectedGraph<Value, NamedEdge> graph;
	
	ClustersResult(Collection<Set<Value>> data, DirectedGraph<Value, NamedEdge> graph) {
		super(data);
		this.graph = graph;
	}

	@Override
	public String getShortReport() {
		return generateReport(true);
	}

	@Override
	public String getExtensiveReport() {
		return generateReport(false);
	}
		
	private String generateReport(boolean overviewOnly) {
		StringBuilder report = new StringBuilder();
		long compCount = 1;
		
		if (overviewOnly) {
			report.append("count: ").append(getData().size());
		}
		
		for (Set<Value> component : getData()) {
			report.append("\ncomponent ").append(compCount).append(", size: ").append(component.size());
			if (!overviewOnly) {
                for (Value resource : component) {
                    report.append("\n\t").append(resource.toString());
                }
			}
			compCount++;
		}
		
		return report.toString();
	}

	@Override
	public Collection<String> getAsDOT() {
		return new GraphExporter(graph).exportSubGraphs(getData());
	}
}
