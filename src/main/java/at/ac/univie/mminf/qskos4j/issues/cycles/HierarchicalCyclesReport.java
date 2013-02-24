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
    private Collection<Set<Value>> data;
	
	public HierarchicalCyclesReport(Collection<Set<Value>> data, DirectedGraph<Value, NamedEdge> graph) {
		super(data);
		this.graph = graph;
        this.data = data;
	}

    @Override
    public void generateDotReport(BufferedWriter writer) throws IOException {
        for (String hierarchyCycleDot : new GraphExporter(graph).exportSubGraphs(data)) {
            writer.write(hierarchyCycleDot);
            writer.newLine();
        }
    }

}
