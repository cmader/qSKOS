package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class HierarchicalCyclesReport extends CollectionReport<Set<Value>> {

	private DirectedGraph<Value, NamedEdge> graph;
	
	public HierarchicalCyclesReport(Collection<Set<Value>> data, DirectedGraph<Value, NamedEdge> graph) {
		super(data);
		this.graph = graph;
	}

    @Override
    public void generateDotReport(BufferedWriter writer) throws IOException {
        for (String hierarchyCycleDot : new GraphExporter(graph).exportSubGraphs(getData())) {
            writer.write(hierarchyCycleDot);
            writer.newLine();
        }
    }

}
