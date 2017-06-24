package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.IRIImpl;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;

import java.util.*;

public class HierarchicalRedundancy extends Issue<CollectionResult<Pair<Resource>>> {

    private Collection<Pair<Resource>> hierarchicalRedundancies;
    private HierarchyGraphBuilder hierarchyGraphBuilder;
    private DirectedGraph<Resource, NamedEdge> hierarchyGraph;

    public HierarchicalRedundancy(HierarchyGraphBuilder hierarchyGraphBuilder) {
        super("hr",
            "Hierarchical Redundancy",
            "Finds broader/narrower relations over multiple hierarchy levels",
            IssueType.ANALYTICAL,
            new IRIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#hierarchical-redundancy"));
        this.hierarchyGraphBuilder = hierarchyGraphBuilder;
    }

    @Override
    protected CollectionResult<Pair<Resource>> invoke() throws RDF4JException {
        hierarchicalRedundancies = new HashSet<>();
        hierarchyGraph = hierarchyGraphBuilder.createGraph();

        Set<NamedEdge> allEdges = new HashSet<>(hierarchyGraph.edgeSet());
        Iterator<NamedEdge> it = new MonitoredIterator<>(allEdges, progressMonitor);

        while (it.hasNext()) {
            NamedEdge edge = it.next();
            Resource source = hierarchyGraph.getEdgeSource(edge);
            Resource target = hierarchyGraph.getEdgeTarget(edge);

            hierarchyGraph.removeEdge(edge);
            List<NamedEdge> path = new DijkstraShortestPath<>(hierarchyGraph, source, target).getPathEdgeList();
            if (path != null && !path.isEmpty()) {
                hierarchicalRedundancies.add(new Pair<>(source, target));
            }
            hierarchyGraph.addEdge(source, target);
        }

        return new CollectionResult<>(hierarchicalRedundancies);
    }

    @Override
    public void setRepositoryConnection(RepositoryConnection repCon) {
        hierarchyGraphBuilder.setRepositoryConnection(repCon);
        super.setRepositoryConnection(repCon);
    }

}
