package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
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
public class HierarchicalCycles extends Issue<Collection<Set<Value>>> {

    private final Logger logger = LoggerFactory.getLogger(HierarchicalCycles.class);

    private DirectedGraph<Value, NamedEdge> hierarchyGraph;
    private HierarchyGraphBuilder hierarchyGraphBuilder;

    public HierarchicalCycles(HierarchyGraphBuilder hierarchyGraphBuilder) {
        super("chr",
              "Cyclic Hierarchical Relations",
              "Finds all hierarchy cycle containing components",
              IssueType.ANALYTICAL
        );
        this.hierarchyGraphBuilder = hierarchyGraphBuilder;
    }

    @Override
    protected Collection<Set<Value>> prepareData() throws OpenRDFException {
        hierarchyGraph = hierarchyGraphBuilder.createGraph();
        return findCycleContainingComponents();
    }

    @Override
    protected Report prepareReport(Collection<Set<Value>> preparedData) {
        return new HierarchicalCyclesReport(preparedData, hierarchyGraph);
    }

    private List<Set<Value>> findCycleContainingComponents() {
        logger.debug("Finding cycles");

        Set<Value> nodesInCycles = new CycleDetector<Value, NamedEdge>(hierarchyGraph).findCycles();
        return trackNodesInCycles(nodesInCycles);
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

    @Override
    public void setRepositoryConnection(RepositoryConnection repCon) {
        hierarchyGraphBuilder.setRepositoryConnection(repCon);
        super.setRepositoryConnection(repCon);
    }
}
