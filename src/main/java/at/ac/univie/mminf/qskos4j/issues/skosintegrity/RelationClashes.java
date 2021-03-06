package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Relation_Clashes">Associative vs. Hierarchical Relation Clashes</a>.
 */
public class RelationClashes extends Issue<CollectionResult<Tuple<Resource>>> {

    private HierarchyGraphBuilder hierarchyGraphBuilder;

    public RelationClashes(HierarchyGraphBuilder hierarchyGraphBuilder) {
        super(new IssueDescriptor.Builder("rc",
              "Relation Clashes",
              "Covers condition S27 from the SKOS reference document (Associative vs. Hierarchical Relation Clashes)",
              IssueDescriptor.IssueType.ANALYTICAL)
                .weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#relation-clashes")
                .build()
        );

        this.hierarchyGraphBuilder = hierarchyGraphBuilder;
    }

    @Override
    protected CollectionResult<Tuple<Resource>> invoke() throws RDF4JException {
        Graph<Resource, NamedEdge> hierarchyGraph = hierarchyGraphBuilder.createGraph();

        Collection<Tuple<Resource>> clashes = new HashSet<Tuple<Resource>>();

        Iterator<Tuple<Resource>> it = new MonitoredIterator<Tuple<Resource>>(
                findRelatedConcepts(),
                progressMonitor);

        while (it.hasNext()) {
            Tuple<Resource> conceptPair = it.next();
            try {
                if (pathExists(hierarchyGraph, conceptPair)) {
                    clashes.add(conceptPair);
                }
            }
            catch (IllegalArgumentException e) {
                // one of the concepts not in graph, no clash possible
            }
        }

        return new CollectionResult<Tuple<Resource>>(clashes);
    }

    private Collection<Tuple<Resource>> findRelatedConcepts() throws RDF4JException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createRelatedConceptsQuery()).evaluate();
        return TupleQueryResultUtil.createCollectionOfResourcePairs(result, "concept1", "concept2");
    }

    private String createRelatedConceptsQuery() {
        return SparqlPrefix.SKOS +
            "SELECT DISTINCT ?concept1 ?concept2 WHERE {" +
                "?concept1 skos:related|skos:relatedMatch ?concept2 ." +
            "}";
    }

    private boolean pathExists(Graph<Resource, NamedEdge> hierarchyGraph, Tuple<Resource> conceptPair) {
        if (new DijkstraShortestPath<Resource, NamedEdge>(
                hierarchyGraph,
                conceptPair.getFirst(),
                conceptPair.getSecond()).getPathEdgeList() == null)
        {
            return new DijkstraShortestPath<Resource, NamedEdge>(
                    hierarchyGraph,
                    conceptPair.getSecond(),
                    conceptPair.getFirst()).getPathEdgeList() != null;
        }
        return true;
    }

    @Override
    public void setRepositoryConnection(RepositoryConnection repCon) {
        hierarchyGraphBuilder.setRepositoryConnection(repCon);
        super.setRepositoryConnection(repCon);
    }
}
