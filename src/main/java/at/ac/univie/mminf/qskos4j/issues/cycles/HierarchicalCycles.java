package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:26
 *
 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Cyclic_Hierarchical_Relations">Cyclic Hierarchical Relations</a>.
 */
public class HierarchicalCycles extends Issue<CollectionReport<Set<Value>>> {

    private final Logger logger = LoggerFactory.getLogger(HierarchicalCycles.class);

    private enum HierarchyType {BROADER, NARROWER, NONE}

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
    public void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException {
        HierarchyType hierarchyType = getPredicateHierarchyType(statement.getPredicate());
        if (hierarchyType == HierarchyType.NONE) return;

        RepositoryConnection repCon = vocabRepository.getRepository().getConnection();
        try {
            String query = createHierarchicalCycleQuery(statement.getSubject(), statement.getObject(), hierarchyType);
            BooleanQuery hierarchicalQuery = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, query);
            if (hierarchicalQuery.evaluate()) {
                throw new IssueOccursException();
            }
        }
        finally {
            repCon.close();
        }
    }

    private String createHierarchicalCycleQuery(Resource subject, Value object, HierarchyType hierarchyType)
    {
        return SparqlPrefix.SKOS +" "+
            "ASK {<" +object+ "> (" +getHierarchicalProperties(hierarchyType)+ ")* <" +subject+ ">}";
    }

    private String getHierarchicalProperties(HierarchyType hierarchyType) {
        Iterator<URI> broaderIt = Arrays.asList(SkosOntology.SKOS_BROADER_PROPERTIES).iterator();
        Iterator<URI> narrowerIt = Arrays.asList(SkosOntology.SKOS_NARROWER_PROPERTIES).iterator();

        switch (hierarchyType) {
            case BROADER:
                return concatWithOrOperator(broaderIt, false) +"|"+ concatWithOrOperator(narrowerIt, true);

            case NARROWER:
                return concatWithOrOperator(narrowerIt, false) +"|"+ concatWithOrOperator(broaderIt, true);

            default:
                return "";
        }
    }

    private String concatWithOrOperator(Iterator<URI> iterator, boolean addInversePrefix) {
        String concatedEntries = "";
        while (iterator.hasNext()) {
            concatedEntries += (addInversePrefix ? "^" : "") +"<"+ iterator.next() +">"+ (iterator.hasNext() ? "|" : "");
        }
        return concatedEntries;
    }

    private HierarchyType getPredicateHierarchyType(URI predicate) {
        if (Arrays.asList(SkosOntology.SKOS_BROADER_PROPERTIES).contains(predicate)) return HierarchyType.BROADER;
        if (Arrays.asList(SkosOntology.SKOS_NARROWER_PROPERTIES).contains(predicate)) return HierarchyType.NARROWER;

        return HierarchyType.NONE;
    }

}
