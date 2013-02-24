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
    private Collection<Set<Value>> data;
	
	ClustersReport(Collection<Set<Value>> data, DirectedGraph<Value, NamedEdge> graph) {
		super(data);
		this.graph = graph;
        this.data = data;
	}

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException {
		StringBuilder report = new StringBuilder();
		long compCount = 1;
		
		if (style == ReportStyle.SHORT) {
			report.append("count: ").append(data.size()).append("\n");
		}

        Iterator<Set<Value>> componentIt = data.iterator();
        while (componentIt.hasNext()) {
            Set<Value> component = componentIt.next();

			report.append("component ").append(compCount).append(", size: ").append(component.size());
			if (style == ReportStyle.EXTENSIVE) {
                for (Value resource : component) {
                    report.append("\n\t").append(resource.toString());
                }
			}
			compCount++;

            if (componentIt.hasNext()) report.append("\n");
		}
		
		osw.write(report.toString());
	}

    @Override
    public void generateDotReport(BufferedWriter writer) throws IOException {
        for (String dotGraph : new GraphExporter(graph).exportSubGraphs(data)) {
            writer.write(dotGraph);
            writer.newLine();
        }
    }

}
