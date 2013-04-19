package at.ac.univie.mminf.qskos4j.issues.labels.util;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

public class ResourceLabelsCollector {

    private final Logger logger = LoggerFactory.getLogger(ResourceLabelsCollector.class);

    private RepositoryConnection repCon;
    private Collection<LabeledConcept> labeledResources;

    public Collection<LabeledConcept> getLabeledConcepts() throws OpenRDFException {
        labeledResources = new HashSet<LabeledConcept>();
        createLabeledResources();
        return labeledResources;
    }

    private void createLabeledResources() {
        for (LabelType labelType : LabelType.values()) {
            String labelQuery = createLabelQuery(labelType);
            try {
                TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, labelQuery).evaluate();
                addResultToLabels(labelType, result);
            }
            catch (OpenRDFException e) {
                logger.error("error querying repository: " +labelQuery);
            }
        }
    }

    private String createLabelQuery(LabelType labelType) {
        return SparqlPrefix.SKOS +
            "SELECT DISTINCT ?resource ?label"+
            "{" +
                "?resource "+labelType.getSkosProperty()+" ?label ." +
            "}";
    }

    private void addResultToLabels(LabelType labelType, TupleQueryResult result) throws QueryEvaluationException
    {
        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Resource resource = (Resource) queryResult.getValue("resource");

            try {
                Literal label = (Literal) queryResult.getValue("label");

                if (label != null) {
                    labeledResources.add(new LabeledConcept(resource, label, labelType));
                }
            }
            catch (ClassCastException e) {
                logger.info("literal label expected for resource " +resource.toString()+ ", " +e.toString());
            }
        }
    }

    public void setRepositoryConnection(RepositoryConnection repCon) {
        this.repCon = repCon;
    }

}
