package at.ac.univie.mminf.qskos4j.issues.clusters;

import at.ac.univie.mminf.qskos4j.result.ResourceCollectionsResult;
import at.ac.univie.mminf.qskos4j.util.graph.GraphExporter;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

public class ClustersResult extends ResourceCollectionsResult
{
	private DirectedGraph<Resource, NamedEdge> graph;

	ClustersResult(Collection<Collection<Resource>> data, DirectedGraph<Resource, NamedEdge> graph) {
        super(data, "Cluster");

        this.graph = graph;
	}

    @Override
    public void generateDotReport(BufferedWriter writer) throws IOException {
        for (String dotGraph : new GraphExporter(graph).exportDotGraphs(getData())) {
            writer.write(dotGraph);
            writer.newLine();
        }
    }

}
