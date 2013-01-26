package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledResource;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NonDisjointLabelsFinder extends Issue {

    private Map<Literal, LabelConflict> nonDisjointLabels;
    private ResourceLabelsCollector resourceLabelsCollector;

    public NonDisjointLabelsFinder(VocabRepository vocabRepository, ResourceLabelsCollector resourceLabelsCollector) {
        super(vocabRepository);
        this.resourceLabelsCollector = resourceLabelsCollector;
    }

    public CollectionResult<LabelConflict> findDisjointLabelsViolations() throws OpenRDFException
    {
        if (nonDisjointLabels == null) {
            findNonDisjointLabels();
        }

        return new CollectionResult<LabelConflict>(nonDisjointLabels.values());
    }

    private void findNonDisjointLabels() {
        Map<Literal, Collection<LabeledResource>> resourcesByLabel = orderResourcesByLabel();
        extractNonDisjointConflicts(resourcesByLabel);
    }

    private Map<Literal, Collection<LabeledResource>> orderResourcesByLabel() {
        Map<Literal, Collection<LabeledResource>> resourcesByLabel = new HashMap<Literal, Collection<LabeledResource>>();

        for (LabeledResource labeledResource : resourceLabelsCollector.getLabeledResources()) {
            Literal literal = labeledResource.getLiteral();
            Collection<LabeledResource> resourcesForLiteral = resourcesByLabel.get(literal);
            if (resourcesForLiteral == null) {
                resourcesForLiteral = new HashSet<LabeledResource>();
                resourcesByLabel.put(literal, resourcesForLiteral);
            }

            resourcesForLiteral.add(labeledResource);
        }

        return resourcesByLabel;
    }

    private void extractNonDisjointConflicts(Map<Literal, Collection<LabeledResource>> resourcesByLabel) {
        nonDisjointLabels = new HashMap<Literal, LabelConflict>();

        for (Map.Entry<Literal, Collection<LabeledResource>> entry : resourcesByLabel.entrySet()) {
            LabelConflict labelConflict = findNonDisjointConflict(entry.getValue());
            if (labelConflict != null) {
                nonDisjointLabels.put(entry.getKey(), labelConflict);
            }
        }
    }

    private LabelConflict findNonDisjointConflict(Collection<LabeledResource> identicallyLabeledResources) {
        LabelConflict labelConflict = null;

        for (LabeledResource labeledResource : identicallyLabeledResources) {
            for (LabeledResource otherLabeledResource : identicallyLabeledResources) {
                if (hasNonDisjointConflict(labeledResource, otherLabeledResource)) {
                    if (labelConflict == null) labelConflict = new LabelConflict();
                    labelConflict.add(labeledResource);
                    labelConflict.add(otherLabeledResource);
                }
            }
        }

        return labelConflict;
    }

    private boolean hasNonDisjointConflict(LabeledResource resource1, LabeledResource resource2) {
        return (resource1.getResource() == resource2.getResource()) && (resource1.getLabelType() != resource2.getLabelType());
    }

}
