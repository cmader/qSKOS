package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.ArrayList;
import java.util.Arrays;
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
        return new HierarchicalCyclesReport(findCycleContainingComponents(), hierarchyGraph);
    }

    private List<Set<Value>> findCycleContainingComponents() {
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
    public void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException
    {
        if (hierarchyGraph == null) {
            hierarchyGraph = hierarchyGraphBuilder.createGraph();
        }

        if (subjectAndObjectInGraph(statement) && isHierarchicalPredicate(statement.getPredicate())) {
            if (causesCycle(statement)) {
                throw new IssueOccursException();
            }
        }
    }

    private boolean subjectAndObjectInGraph(Statement statement) {
        return hierarchyGraph.containsVertex(statement.getSubject()) &&
               hierarchyGraph.containsVertex(statement.getObject());
    }

    private boolean isHierarchicalPredicate(URI predicate) {
        return Arrays.asList(SkosOntology.SKOS_BROADER_PROPERTIES).contains(predicate) ||
               Arrays.asList(SkosOntology.SKOS_NARROWER_PROPERTIES).contains(predicate);
    }

    public boolean causesCycle(Statement statement) throws OpenRDFException {
        NamedEdge newEdge = hierarchyGraph.addEdge(statement.getSubject(), statement.getObject());
        NamedEdge newInverseEdge = hierarchyGraph.addEdge(statement.getObject(), statement.getSubject());

        try {
            return subjectAndObjectInSameComponent(statement, findCycleContainingComponents());
        }
        finally {
            hierarchyGraph.removeEdge(newEdge);
            hierarchyGraph.removeEdge(newInverseEdge);
        }
    }

    private boolean subjectAndObjectInSameComponent(Statement statement, List<Set<Value>> cycleContainingComponents) {
        Resource subject = statement.getSubject();
        Value object = statement.getObject();

        for (Set<Value> component : cycleContainingComponents) {
            boolean subjectFound = false, objectFound = false;

            for (Value vertex : component) {
                if (vertex.equals(subject)) subjectFound = true;
                if (vertex.equals(object)) objectFound = true;
                if (subjectFound && objectFound) return true;
            }
        }

        return false;
    }

    @Override
    protected void reset() {
        super.reset();
        hierarchyGraph = null;
    }

    // only for testing
    public int[] getHierarchyGraphSize() {
        int edgeCount = hierarchyGraph.edgeSet().size();
        int vertexCount = hierarchyGraph.vertexSet().size();
        return new int[] {vertexCount, edgeCount};
    }
}
