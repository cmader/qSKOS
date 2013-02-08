package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Finds top concepts that have broader concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Top_Concepts_Having_Broader_Concepts">Top Concepts Having Broader Concepts</a>
 */
public class TopConceptsHavingBroaderConcepts extends Issue<CollectionReport<Value>> {

    public TopConceptsHavingBroaderConcepts(VocabRepository vocabRepo) {
        super(vocabRepo,
              "tchbc",
              "Top Concepts Having Broader Concepts",
              "Finds top concepts internal to the vocabulary hierarchy tree",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionReport<Value> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createTopConceptsHavingBroaderConceptQuery());
        return new CollectionReport<Value>(createUriResultList(result));
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
