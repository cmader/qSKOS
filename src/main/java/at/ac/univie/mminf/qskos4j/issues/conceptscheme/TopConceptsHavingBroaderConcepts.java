package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Finds top concepts that have broader concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Top_Concepts_Having_Broader_Concepts">Top Concepts Having Broader Concepts</a>
 */
public class TopConceptsHavingBroaderConcepts extends Issue<Collection<Value>> {

    public TopConceptsHavingBroaderConcepts() {
        super("tchbc",
              "Top Concepts Having Broader Concepts",
              "Finds top concepts internal to the vocabulary hierarchy tree",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#top-concepts-having-broader-concepts")
        );
    }

    @Override
    protected Collection<Value> computeResult() throws OpenRDFException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createTopConceptsHavingBroaderConceptQuery());
        return createUriResultList(query.evaluate());
    }

    @Override
    protected Report generateReport(Collection<Value> preparedData) {
        return new CollectionReport<Value>(preparedData);
    }

    private String createTopConceptsHavingBroaderConceptQuery() {
        return SparqlPrefix.SKOS +
                "SELECT DISTINCT ?topConcept WHERE " +
                "{" +
                "{?topConcept skos:topConceptOf ?conceptScheme1}" +
                "UNION" +
                "{?conceptScheme2 skos:hasTopConcept ?topConcept}" +
                "?topConcept skos:broader|skos:broaderTransitive|^skos:narrower|^skos:narrowerTransitive ?broaderConcept ." +
                "}";
    }

    private Collection<Value> createUriResultList(TupleQueryResult result) throws OpenRDFException
    {
        List<Value> resultList = new ArrayList<Value>();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            URI resource = (URI) queryResult.getValue("topConcept");
            resultList.add(resource);
        }

        return resultList;
    }
}
