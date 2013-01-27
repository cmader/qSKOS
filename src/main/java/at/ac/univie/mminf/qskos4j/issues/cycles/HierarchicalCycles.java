package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.custom.HierarchyCycleResult;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:26
 *
 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Cyclic_Hierarchical_Relations">Cyclic Hierarchical Relations</a>.
 */
public class HierarchicalCycles extends Issue<CollectionResult<Set<Resource>>> {

    private DirectedGraph<Resource, NamedEdge> hierarchyGraph;
    private List<Set<Resource>> cycleContainingComponents;

    public HierarchicalCycles() {
        super("chr",
              "Cyclic Hierarchical Relations",
              "Finds all hierarchy cycle containing components",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionResult<Set<Resource>> invoke() throws OpenRDFException {
        Set<Resource> nodesInCycles = new CycleDetector<Resource, NamedEdge>(hierarchyGraph).findCycles();
        cycleContainingComponents = trackNodesInCycles(nodesInCycles);
        return new HierarchyCycleResult(cycleContainingComponents, hierarchyGraph);
    }

    private List<Set<Resource>> trackNodesInCycles(Set<Resource> nodesInCycles)
    {
        List<Set<Resource>> ret = new ArrayList<Set<Resource>>();
        List<Set<Resource>> stronglyConnectedSets =
                new StrongConnectivityInspector<Resource, NamedEdge>(hierarchyGraph).stronglyConnectedSets();

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
}
