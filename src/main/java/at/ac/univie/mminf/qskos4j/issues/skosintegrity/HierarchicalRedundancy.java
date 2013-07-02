package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashSet;

public class HierarchicalRedundancy extends Issue<Collection<Pair<Resource>>> {

    private String HIER_PROPERTIES = "skos:broaderTransitive|^skos:narrowerTransitive";
    private Collection<Pair<Resource>> hierarchicalRedundancies;

    public HierarchicalRedundancy() {
        super("hr",
            "Hierarchical Redundancy",
            "Finds broader/narrower relations over multiple hierarchy levels",
            IssueType.ANALYTICAL);
    }

    @Override
    protected Collection<Pair<Resource>> computeResult() throws OpenRDFException {
        hierarchicalRedundancies = new HashSet<Pair<Resource>>();

        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createQuery()).evaluate();
        while (result.hasNext()) {
            BindingSet bs = result.next();
            Resource concept = (Resource) bs.getValue("concept");
            Resource otherConcept = (Resource) bs.getValue("otherConcept");

            hierarchicalRedundancies.add(new Pair<Resource>(concept, otherConcept));
        }

        return hierarchicalRedundancies;
    }

    private String createQuery() {
        return SparqlPrefix.SKOS + "SELECT ?concept ?otherConcept WHERE {" +
            "?concept " +HIER_PROPERTIES+" ?otherConcept . " +
            "?concept ("+HIER_PROPERTIES+")+ ?imConcept ." +
            "?imConcept ("+HIER_PROPERTIES+") ?otherConcept ." +
        "}";
    }

    @Override
    protected Report generateReport(Collection<Pair<Resource>> preparedData) {
        return new CollectionReport<Pair<Resource>>(hierarchicalRedundancies);
    }

}
