package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labels.util.EmptyLabelsResult;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.*;

public class EmptyLabeledResources extends Issue<EmptyLabelsResult> {

    private Map<Resource, Collection<LabelType>> result;

    public EmptyLabeledResources() {
        super("el", "Empty Labels", "Finds empty labels or labels containing only whitespaces", IssueType.ANALYTICAL);
    }

    @Override
    protected EmptyLabelsResult invoke() throws OpenRDFException {
        result = new HashMap<>();

        TupleQueryResult result = repCon.prepareTupleQuery(
                QueryLanguage.SPARQL,
                createLexicalLabelQuery()
        ).evaluate();

        while (result.hasNext()) {
            BindingSet bs = result.next();
            Resource resource = (Resource) bs.getBinding("resource").getValue();
            URI labelType = (URI) bs.getBinding("labelType").getValue();
            Value literal = bs.getBinding("literal").getValue();

            if (literal.stringValue().trim().isEmpty()) {
                addToResults(resource, LabelType.getFromUri(labelType));
            }
        }

        return new EmptyLabelsResult(this.result);
    }

    private String createLexicalLabelQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.DCTERMS +" "+ SparqlPrefix.RDFS +" "+ SparqlPrefix.DC+
                "SELECT ?resource ?labelType ?literal WHERE " +
                "{" +
                    "?resource ?labelType ?literal ." +
                    "FILTER (?labelType IN (rdfs:label, dc:title, dcterms:title, skos:prefLabel, skos:altLabel, skos:hiddenLabel))" +
                "}";
    }

    private void addToResults(Resource resource, LabelType labelType) {
        Collection<LabelType> values = result.get(resource);
        if (values == null) {
            values = new HashSet<>();
            result.put(resource, values);
        }
        values.add(labelType);
    }
}
