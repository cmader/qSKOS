package at.ac.univie.mminf.qskos4j.issues.labelissues.util;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

public class ResourceLabelsCollector {

    private final Logger logger = LoggerFactory.getLogger(ResourceLabelsCollector.class);

    private Collection<LabeledResource> labeledResources;
    private VocabRepository vocabRepository;

    public ResourceLabelsCollector(VocabRepository vocabRepository) {
        this.vocabRepository = vocabRepository;
    }

    public Collection<LabeledResource> getLabeledResources() {
        if (labeledResources == null) {
            createLabeledResources();
        }
        return labeledResources;
    }

    private void createLabeledResources() {
        labeledResources = new HashSet<LabeledResource>();

        for (LabelType labelType : LabelType.values()) {
            String labelQuery = createLabelQuery(labelType);

            try {
                TupleQueryResult result = vocabRepository.query(labelQuery);
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
                    labeledResources.add(new LabeledResource(resource, label, labelType));
                }
            }
            catch (ClassCastException e) {
                logger.info("literal label expected for resource " +resource.toString()+ ", " +e.toString());
            }
        }
    }
}
