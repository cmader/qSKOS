package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.repository.RepositoryConnection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Finds concepts having identical entries for prefLabel, altLabel or hiddenLabel (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Disjoint_Labels_Violation">Disjoint Labels Violation</a>
 * ).
 */
public class DisjointLabelsViolations extends Issue<Map<Literal, LabelConflict>> {

    private Map<Literal, LabelConflict> nonDisjointLabels;
    private ResourceLabelsCollector resourceLabelsCollector;

    public DisjointLabelsViolations(ResourceLabelsCollector resourceLabelsCollector) {
        super("dlv",
            "Disjoint Labels Violation",
            "Finds resources with identical entries for different label types",
            IssueType.ANALYTICAL);

        this.resourceLabelsCollector = resourceLabelsCollector;
    }

    @Override
    protected Map<Literal, LabelConflict> prepareData() throws OpenRDFException {
        findNonDisjointLabels();
        return nonDisjointLabels;
    }

    @Override
    protected Report prepareReport(Map<Literal, LabelConflict> preparedData) {
        return new CollectionReport<LabelConflict>(nonDisjointLabels.values());
    }

    private void findNonDisjointLabels() throws OpenRDFException {
        Map<Literal, Collection<LabeledConcept>> resourcesByLabel = orderResourcesByLabel();
        extractNonDisjointConflicts(resourcesByLabel);
    }

    private Map<Literal, Collection<LabeledConcept>> orderResourcesByLabel() throws OpenRDFException {
        Map<Literal, Collection<LabeledConcept>> resourcesByLabel = new HashMap<Literal, Collection<LabeledConcept>>();

        for (LabeledConcept labeledResource : resourceLabelsCollector.getLabeledConcepts()) {
            Literal literal = new LiteralImpl(
                labeledResource.getLiteral().getLabel().toUpperCase(),
                labeledResource.getLiteral().getLanguage());

            Collection<LabeledConcept> resourcesForLiteral = resourcesByLabel.get(literal);
            if (resourcesForLiteral == null) {
                resourcesForLiteral = new HashSet<LabeledConcept>();
                resourcesByLabel.put(literal, resourcesForLiteral);
            }

            resourcesForLiteral.add(labeledResource);
        }

        return resourcesByLabel;
    }

    private void extractNonDisjointConflicts(Map<Literal, Collection<LabeledConcept>> resourcesByLabel) {
        nonDisjointLabels = new HashMap<Literal, LabelConflict>();

        for (Map.Entry<Literal, Collection<LabeledConcept>> entry : resourcesByLabel.entrySet()) {
            LabelConflict labelConflict = findNonDisjointConflict(entry.getValue());
            if (labelConflict != null) {
                nonDisjointLabels.put(entry.getKey(), labelConflict);
            }
        }
    }

    private LabelConflict findNonDisjointConflict(Collection<LabeledConcept> identicallyLabeledResources) {
        LabelConflict labelConflict = null;

        for (LabeledConcept labeledResource : identicallyLabeledResources) {
            for (LabeledConcept otherLabeledResource : identicallyLabeledResources) {
                if (hasNonDisjointConflict(labeledResource, otherLabeledResource)) {
                    if (labelConflict == null) labelConflict = new LabelConflict();
                    labelConflict.add(labeledResource);
                    labelConflict.add(otherLabeledResource);
                }
            }
        }

        return labelConflict;
    }

    private boolean hasNonDisjointConflict(LabeledConcept resource1, LabeledConcept resource2) {
        return (resource1.getConcept() == resource2.getConcept()) && (resource1.getLabelType() != resource2.getLabelType());
    }

    @Override
    public void setRepositoryConnection(RepositoryConnection repCon) {
        resourceLabelsCollector.setRepositoryConnection(repCon);
        super.setRepositoryConnection(repCon);
    }
}
