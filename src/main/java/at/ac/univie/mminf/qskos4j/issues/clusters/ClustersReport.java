package at.ac.univie.mminf.qskos4j.issues.clusters;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
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
			report.append("count: ").append(getData().size()).append("\n");
		}

        Iterator<Set<Value>> componentIt = getData().iterator();
        while (componentIt.hasNext()) {
            Set<Value> component = componentIt.next();

			report.append("component ").append(compCount).append(", size: ").append(component.size());
			if (style == ReportStyle.EXTENSIVE) {
                Iterator<Value> resourceIt = component.iterator();
                while (resourceIt.hasNext()) {
                    report.append("\n\t").append(resourceIt.next().toString());
                }
			}
			compCount++;

            if (componentIt.hasNext()) report.append("\n");
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
