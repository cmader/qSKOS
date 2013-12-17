package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.result.NumberResult;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 13:58
 *
 * Finds the number of relations involving SKOS lexical labels (prefLabel, altLabel, hiddenLabel).
 *
 */
public class LexicalRelations extends Issue<NumberResult<Long>> {

    private final Logger logger = LoggerFactory.getLogger(LexicalRelations.class);

    private InvolvedConcepts involvedConcepts;

    public LexicalRelations(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts,
            "cl",
            "Concept Labels",
            "Counts the number of relations between all concepts and lexical labels (prefLabel, altLabel, hiddenLabel and subproperties thereof)",
            IssueType.STATISTICAL
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected NumberResult<Long> invoke() throws OpenRDFException {
        long relationsCount = 0;

        for (Value concept : involvedConcepts.getResult().getData()) {
            try {
                TupleQueryResult result = repCon.prepareTupleQuery(
                        QueryLanguage.SPARQL,
                        createLexicalLabelQuery(concept)
                    ).evaluate();

                relationsCount += TupleQueryResultUtil.countResults(result);
            }
            catch (OpenRDFException e) {
                logger.error("Error finding labels for concept '" +concept+ "'", e);
            }
        }

        return new NumberResult<Long>(relationsCount);
    }

    private String createLexicalLabelQuery(Value concept) {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?skosLabelType ?literal WHERE " +
                "{" +
                    "{<" +concept.stringValue()+ "> ?skosLabelType ?literal} " +
                    "FILTER (?skosLabelType IN (skos:prefLabel, skos:altLabel, skos:hiddenLabel))" +
                "}";
    }

}
