package at.ac.univie.mminf.qskos4j.issues.clusters;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ClustersReport extends CollectionReport<Set<Value>>
{
	private DirectedGraph<Value, NamedEdge> graph;
	
	ClustersReport(Collection<Set<Value>> data, DirectedGraph<Value, NamedEdge> graph) {
		super(data);
		this.graph = graph;
	}

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException {
		StringBuilder report = new StringBuilder();
		long compCount = 1;
		
		if (style == ReportStyle.SHORT) {
			report.append("count: ").append(getData().size());
		}
		
		for (Set<Value> component : getData()) {
			report.append("\ncomponent ").append(compCount).append(", size: ").append(component.size());
			if (style == ReportStyle.EXTENSIVE) {
                for (Value resource : component) {
                    report.append("\n\t").append(resource.toString());
                }
			}
			compCount++;
		}
		
		osw.write(report.toString());
	}

    @Override
    public void generateDotReport(BufferedWriter writer) throws IOException {
        for (String dotGraph : new GraphExporter(graph).exportSubGraphs(getData())) {
            writer.write(dotGraph);
            writer.newLine();
        }
    }

}
