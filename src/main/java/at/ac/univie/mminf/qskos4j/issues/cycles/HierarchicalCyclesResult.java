package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.result.ResourceCollectionsResult;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

public class HierarchicalCyclesResult extends ResourceCollectionsResult {

    private DirectedGraph<Resource, NamedEdge> graph;

    public HierarchicalCyclesResult(
            Collection<Collection<Resource>> data,
            DirectedGraph<Resource, NamedEdge> graph)
    {
        super(data, "Cycle");

        this.graph = graph;
    }

    @Override
    public void generateDotReport(BufferedWriter writer) throws IOException {
        for (String hierarchyCycleDot : new GraphExporter(graph).exportDotGraphs(getData())) {
            writer.write(hierarchyCycleDot);
            writer.newLine();
        }
    }

}
