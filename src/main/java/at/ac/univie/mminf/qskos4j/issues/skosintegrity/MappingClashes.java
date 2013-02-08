package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;

/**
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Mapping_Clashes">Exact vs. Associative and Hierarchical Mapping Clashes</a>.
 */
public class MappingClashes extends Issue<CollectionReport<Pair<Value>>> {

    public MappingClashes(VocabRepository vocabRepo) {
        super(vocabRepo,
              "mc",
              "Mapping Clashes",
              "Covers condition S46 from the SKOS reference document (Exact vs. Associative and Hierarchical Mapping Clashes)",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionReport<Pair<Value>> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createExVsAssMappingQuery());
        Collection<Pair<Value>> exactVsAssMappingClashes = TupleQueryResultUtil.createCollectionOfValuePairs(result, "concept1", "concept2");

        return new CollectionReport<Pair<Value>>(exactVsAssMappingClashes);
    }

    private String createExVsAssMappingQuery() {
        return SparqlPrefix.SKOS +
            "SELECT ?concept1 ?concept2 WHERE {" +
                "?concept1 skos:exactMatch ?concept2 ."+
                "?concept1 skos:broadMatch|skos:narrowMatch|skos:relatedMatch ?concept2 ." +
            "}";
    }
}
