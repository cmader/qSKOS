package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labels.util.EmptyLabelsResult;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.util.*;

public class EmptyLabeledResources extends Issue<EmptyLabelsResult> {

    private Map<Resource, Collection<LabelType>> result;

    public EmptyLabeledResources() {
        super("el", "Empty Labels", "Finds empty labels or labels containing only whitespaces", IssueType.ANALYTICAL);
    }

    @Override
    protected EmptyLabelsResult invoke() throws RDF4JException {
        result = new HashMap<Resource, Collection<LabelType>>();

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
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.DCTERMS +" "+ SparqlPrefix.RDFS +
                "SELECT ?resource ?labelType ?literal WHERE " +
                "{" +
                    "?resource ?labelType ?literal ." +
                    "FILTER (?labelType IN (dcterms:title, skos:prefLabel, skos:altLabel, skos:hiddenLabel))" +
                "}";
    }

    private void addToResults(Resource resource, LabelType labelType) {
        Collection<LabelType> values = result.get(resource);
        if (values == null) {
            values = new HashSet<LabelType>();
            result.put(resource, values);
        }
        values.add(labelType);
    }
}
