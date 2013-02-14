package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

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
public class HierarchicalCycles extends Issue<CollectionReport<Set<Value>>> {

    private DirectedGraph<Value, NamedEdge> hierarchyGraph;
    private HierarchyGraphBuilder hierarchyGraphBuilder;

    public HierarchicalCycles(HierarchyGraphBuilder hierarchyGraphBuilder) {
        super(hierarchyGraphBuilder.getVocabRepository(),
              "chr",
              "Cyclic Hierarchical Relations",
              "Finds all hierarchy cycle containing components",
              IssueType.ANALYTICAL
        );
        this.hierarchyGraphBuilder = hierarchyGraphBuilder;
    }

    @Override
    protected CollectionReport<Set<Value>> invoke() throws OpenRDFException {
        hierarchyGraph = hierarchyGraphBuilder.createGraph();
        Set<Value> nodesInCycles = new CycleDetector<Value, NamedEdge>(hierarchyGraph).findCycles();
        List<Set<Value>> cycleContainingComponents = trackNodesInCycles(nodesInCycles);

        return new HierarchicalCyclesReport(cycleContainingComponents, hierarchyGraph);
    }

    private List<Set<Value>> trackNodesInCycles(Set<Value> nodesInCycles)
    {
        List<Set<Value>> ret = new ArrayList<Set<Value>>();
        List<Set<Value>> stronglyConnectedSets =
                new StrongConnectivityInspector<Value, NamedEdge>(hierarchyGraph).stronglyConnectedSets();

        for (Value node : nodesInCycles) {
            for (Set<Value> stronglyConnectedSet : stronglyConnectedSets) {
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
