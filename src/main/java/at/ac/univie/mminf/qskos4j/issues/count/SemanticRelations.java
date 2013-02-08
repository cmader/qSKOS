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
 * Time: 14:01
 *
 * Finds the number of triples involving (subproperties of) skos:semanticRelation.
 */
public class SemanticRelations extends Issue<NumberReport<Long>> {

    public SemanticRelations(VocabRepository vocabRepo) {
        super(vocabRepo,
              "sr",
              "Semantic Relations Count",
              "Counts the number of relations between concepts (skos:semanticRelation and subproperties thereof)",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberReport<Long> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createSemanticRelationsQuery());
        return new NumberReport<Long>(TupleQueryResultUtil.countResults(result));
    }

    private String createSemanticRelationsQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT * WHERE {" +
                "?concept ?relationType ?otherConcept ."+
                "?relationType rdfs:subPropertyOf* skos:semanticRelation ."+
            "}";
    }

}
