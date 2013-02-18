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
 * Finds the number of triples that assign concepts to concept schemes or lists.
 */
public class AggregationRelations extends Issue<NumberReport<Long>> {

    private final String AGGREGATION_RELATIONS =
        "skos:topConceptOf, skos:hasTopConcept, skos:inScheme, skos:member, skos:memberList";

    public AggregationRelations(VocabRepository vocabRepo) {
        super(vocabRepo,
              "ar",
              "Aggregation Relations Count",
              "Counts the statements relating resources to ConceptSchemes or Collections",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberReport<Long> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createAggregationRelationsQuery());
        return new NumberReport<Long>(TupleQueryResultUtil.countResults(result));
    }

    private String createAggregationRelationsQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT * WHERE {" +
                "{" +
                    "?res1 ?aggregationRelation ?res2 ." +
                "}" +
                "UNION" +
                "{" +
                    "?res1 ?p ?res2 . " +
                    "?p rdfs:subPropertyOf ?aggregationRelation ." +
                "}"+
                "FILTER (?aggregationRelation IN ("+ AGGREGATION_RELATIONS+ "))" +
            "}";
    }

}
