package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.NumberResult;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:23
 *
 * Finds the number of triples that assign concepts to concept schemes or lists.
 */
public class AggregationRelations extends Issue<NumberResult<Long>> {

    private final String AGGREGATION_RELATIONS =
        "skos:topConceptOf, skos:hasTopConcept, skos:inScheme, skos:member, skos:memberList";

    public AggregationRelations() {
        super("ar",
              "Aggregation Relations Count",
              "Counts the statements relating resources to ConceptSchemes or Collections",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberResult<Long> invoke() throws OpenRDFException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createAggregationRelationsQuery());
        return new NumberResult<Long>(TupleQueryResultUtil.countResults(query.evaluate()));
    }

    private String createAggregationRelationsQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT * WHERE {" +
                "{" +
                    "?res1 ?aggregationRelation ?res2 ." +
                "}" +
                "FILTER (?aggregationRelation IN ("+ AGGREGATION_RELATIONS+ "))" +
            "}";
    }

}
