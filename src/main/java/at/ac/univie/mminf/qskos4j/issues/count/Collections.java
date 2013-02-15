package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.NumberReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.query.TupleQueryResult;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:23
 *
 * Finds the number of SKOS <a href="http://www.w3.org/TR/skos-reference/#collections">Collections</a>.
 */
public class Collections extends Issue<NumberReport<Long>> {

    public Collections(VocabRepository vocabRepo) {
        super(vocabRepo,
              "cc",
              "Collection Count",
              "Counts the involved Collections",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberReport<Long> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createCollectionsQuery());
        return new NumberReport<Long>(TupleQueryResultUtil.countResults(result));
    }

    private String createCollectionsQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +" "+ SparqlPrefix.RDF +
            "SELECT DISTINCT ?collection WHERE {" +
                "{?collection rdf:type/rdfs:subClassOf* skos:Collection .}" +
                "UNION" +
                "{?collection rdf:type/rdfs:subClassOf* skos:OrderedCollection .}" +
                "UNION" +
                "{?collection rdfs:subPropertyOf*/(skos:member|skos:memberList) ?member .}" +
            "}";
    }
}
