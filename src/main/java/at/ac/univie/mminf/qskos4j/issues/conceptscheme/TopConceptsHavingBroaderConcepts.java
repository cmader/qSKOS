package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Finds top concepts that have broader concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Top_Concepts_Having_Broader_Concepts">Top Concepts Having Broader Concepts</a>
 */
public class TopConceptsHavingBroaderConcepts extends Issue<CollectionResult<Value>> {

    public TopConceptsHavingBroaderConcepts() {
        super("tchbc",
              "Top Concepts Having Broader Concepts",
              "Finds top concepts internal to the vocabulary hierarchy tree",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#top-concepts-having-broader-concepts")
        );
    }

    @Override
    protected CollectionResult<Value> invoke() throws RDF4JException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createTopConceptsHavingBroaderConceptQuery());
        return new CollectionResult<Value>(createUriResultList(query.evaluate()));
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

    private Collection<Value> createUriResultList(TupleQueryResult result) throws RDF4JException
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
