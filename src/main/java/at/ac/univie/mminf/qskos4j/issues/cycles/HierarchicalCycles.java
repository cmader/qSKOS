package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.IRIImpl;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:26
 *
 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Cyclic_Hierarchical_Relations">Cyclic Hierarchical Relations</a>.
 */
public class HierarchicalCycles extends Issue<HierarchicalCyclesResult> {

    private final Logger logger = LoggerFactory.getLogger(HierarchicalCycles.class);

    private DirectedGraph<Resource, NamedEdge> hierarchyGraph;
    private HierarchyGraphBuilder hierarchyGraphBuilder;

    public HierarchicalCycles(HierarchyGraphBuilder hierarchyGraphBuilder) {
        super("chr",
              "Cyclic Hierarchical Relations",
              "Finds concepts that are hierarchically related to each other",
              IssueType.ANALYTICAL,
              new IRIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#cyclic-hierarchical-relations")
        );
        this.hierarchyGraphBuilder = hierarchyGraphBuilder;
    }

    @Override
    protected HierarchicalCyclesResult invoke() throws RDF4JException {
        hierarchyGraph = hierarchyGraphBuilder.createGraph();
        return new HierarchicalCyclesResult(findCycleContainingComponents(), hierarchyGraph);
    }

    private List<Collection<Resource>> findCycleContainingComponents() {
        logger.debug("Finding cycles");

        Set<Resource> nodesInCycles = new CycleDetector<>(hierarchyGraph).findCycles();
        return trackNodesInCycles(nodesInCycles);
    }

    private List<Collection<Resource>> trackNodesInCycles(Set<Resource> nodesInCycles)
    {
        List<Collection<Resource>> ret = new ArrayList<>();
        List<Set<Resource>> stronglyConnectedSets =
                new StrongConnectivityInspector<>(hierarchyGraph).stronglyConnectedSets();

        for (Resource node : nodesInCycles) {
            for (Set<Resource> stronglyConnectedSet : stronglyConnectedSets) {
                if (stronglyConnectedSet.contains(node)) {
                    if (!ret.contains(stronglyConnectedSet)) {
                        ret.add(stronglyConnectedSet);
                    }
                }
            }
        }

        return ret;
    }

    @Override
    public void setRepositoryConnection(RepositoryConnection repCon) {
        hierarchyGraphBuilder.setRepositoryConnection(repCon);
        super.setRepositoryConnection(repCon);
    }
}
