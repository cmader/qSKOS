package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashSet;

public class HierarchicalRedundancy extends Issue<CollectionResult<Tuple<Resource>>> {

    private String HIER_PROPERTIES = "skos:broaderTransitive|^skos:narrowerTransitive";
    private Collection<Tuple<Resource>> hierarchicalRedundancies;

    public HierarchicalRedundancy() {
        super("hr",
            "Hierarchical Redundancy",
            "Finds broader/narrower relations over multiple hierarchy levels",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#hierarchical-redundancy"));
    }

    @Override
    protected CollectionResult<Tuple<Resource>> invoke() throws OpenRDFException {
        hierarchicalRedundancies = new HashSet<Tuple<Resource>>();

        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createQuery()).evaluate();
        while (result.hasNext()) {
            BindingSet bs = result.next();
            Resource concept = (Resource) bs.getValue("concept");
            Resource otherConcept = (Resource) bs.getValue("otherConcept");

            hierarchicalRedundancies.add(new Tuple<Resource>(concept, otherConcept));
        }

        return new CollectionResult<Tuple<Resource>>(hierarchicalRedundancies);
    }

    private String createQuery() {
        return SparqlPrefix.SKOS + "SELECT ?concept ?otherConcept WHERE {" +
            "?concept " +HIER_PROPERTIES+" ?otherConcept . " +
            "?concept "+HIER_PROPERTIES+" ?imConcept ." +
            "?imConcept ("+HIER_PROPERTIES+")+ ?otherConcept ." +
        "}";
    }

}
