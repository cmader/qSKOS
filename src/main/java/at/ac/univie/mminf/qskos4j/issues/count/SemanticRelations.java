package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.NumberReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:01
 *
 * Finds the number of triples involving (subproperties of) skos:semanticRelation.
 */
public class SemanticRelations extends Issue<Long> {

    public SemanticRelations() {
        super("sr",
              "Semantic Relations Count",
              "Counts the number of relations between concepts (skos:semanticRelation and subproperties thereof)",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected Long computeResult() throws OpenRDFException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createSemanticRelationsQuery());
        return TupleQueryResultUtil.countResults(query.evaluate());
    }

    @Override
    protected Report generateReport(Long preparedData) {
        return new NumberReport<Long>(preparedData);
    }

    private String createSemanticRelationsQuery() throws OpenRDFException
    {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT ?relationType WHERE " +
            "{" +
                "?concept ?relationType ?otherConcept ." +
                "?relationType rdfs:subPropertyOf skos:semanticRelation"+
            "}";
    }

}
