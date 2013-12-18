package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.NumberResult;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

public class EmptyLabels extends Issue<CollectionResult<LabeledConcept>> {

    public EmptyLabels() {
        super("el", "Empty Labels", "Finds empty labels or labels containing only whitespaces", IssueType.ANALYTICAL);
    }

    @Override
    protected CollectionResult<Resource> invoke() throws OpenRDFException {
        implement me!
        return null;
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
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.DCTERMS +" "+ SparqlPrefix.RDFS +
                "SELECT DISTINCT ?resource ?literal WHERE " +
                "{" +
                    "?resource ?labelType ?literal ." +
                    "FILTER (?labelType IN (rdfs:label, dcterms:title, skos:prefLabel, skos:altLabel, skos:hiddenLabel))" +
                "}";
    }
}
