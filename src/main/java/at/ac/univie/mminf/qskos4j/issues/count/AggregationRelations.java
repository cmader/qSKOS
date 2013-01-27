package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.NumberResult;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.query.TupleQueryResult;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:23
 *
 * Finds the number of triples that assign concepts to concept schemes or lists.
 */
public class AggregationRelations extends Issue<NumberResult<Long>> {

    public AggregationRelations() {
        super("ar",
              "Aggregation Relations Count",
              "Counts the statements relating resources to ConceptSchemes or Collections",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberResult<Long> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createAggregationRelationsQuery());
        return new NumberResult<Long>(TupleQueryResultUtil.countResults(result));
    }

    private String createAggregationRelationsQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT * WHERE {" +
                "?res1 ?relationType ?res2 ."+
                "{?relationType rdfs:subPropertyOf* skos:topConceptOf}" +
                "UNION" +
                "{?relationType rdfs:subPropertyOf* skos:hasTopConcept}" +
                "UNION" +
                "{?relationType rdfs:subPropertyOf* skos:inScheme}"+
                "UNION" +
                "{?relationType rdfs:subPropertyOf* skos:member}"+
                "UNION" +
                "{?relationType rdfs:subPropertyOf* skos:memberList}"+
            "}";
    }

}
