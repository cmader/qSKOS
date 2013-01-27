package at.ac.univie.mminf.qskos4j.issues.labels;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * * Finds concepts with more than one preferred label (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Inconsistent_Preferred_Labels">Inconsistent Preferred Labels</a>
 */
public class InconsistentPrefLabels extends Issue<CollectionResult<LabelConflict>> {

    private Map<Resource, LabelConflict> ambigPrefLabels;
    private ResourceLabelsCollector resourceLabelsCollector;

    public InconsistentPrefLabels() {
        super("ipl",
              "Inconsistent Preferred Labels",
              "Finds resources with more then one prefLabel per language",
              IssueType.ANALYTICAL);
        resourceLabelsCollector =  new ResourceLabelsCollector();
    }

    @Override
    protected CollectionResult<LabelConflict> invoke() throws OpenRDFException {
        Map<Resource, Collection<LabeledConcept>> prefLabelsByUri = orderPrefLabelsByResource();
        extractPrefLabelConflicts(prefLabelsByUri);

		return new CollectionResult<LabelConflict>(ambigPrefLabels.values());
	}

    private Map<Resource, Collection<LabeledConcept>> orderPrefLabelsByResource() {
        Map<Resource, Collection<LabeledConcept>> prefLabelsByResource = new HashMap<Resource, Collection<LabeledConcept>>();

        for (LabeledConcept labeledResource : resourceLabelsCollector.getLabeledResources()) {
            if (labeledResource.getLabelType() != LabelType.PREF_LABEL) continue;

            Collection<LabeledConcept> labeledResourcesOfUri = prefLabelsByResource.get(labeledResource.getConcept());
            if (labeledResourcesOfUri == null) {
                labeledResourcesOfUri = new ArrayList<LabeledConcept>();
                prefLabelsByResource.put(labeledResource.getConcept(), labeledResourcesOfUri);
            }

            labeledResourcesOfUri.add(labeledResource);
        }

        return prefLabelsByResource;
    }

    private void extractPrefLabelConflicts(Map<Resource, Collection<LabeledConcept>> prefLabelsByResource) {
        ambigPrefLabels = new HashMap<Resource, LabelConflict>();

        for (Map.Entry<Resource, Collection<LabeledConcept>> entry : prefLabelsByResource.entrySet()) {
            LabelConflict labelConflict = findPrefLabelConflict(entry.getValue());
            if (labelConflict != null) {
                ambigPrefLabels.put(entry.getKey(), labelConflict);
            }
        }
    }

    private LabelConflict findPrefLabelConflict(Collection<LabeledConcept> labeledResources) {
        LabelConflict labelConflict = null;

        for (LabeledConcept labeledResource : labeledResources) {
            for (LabeledConcept otherLabeledResource : labeledResources) {
                if (hasPrefLabelConflict(labeledResource, otherLabeledResource)) {
                    if (labelConflict == null) labelConflict = new LabelConflict();
                    labelConflict.add(labeledResource);
                    labelConflict.add(otherLabeledResource);
                }
            }
        }

        return labelConflict;
    }

    private boolean hasPrefLabelConflict(LabeledConcept resource1, LabeledConcept resource2) {
        if (resource1 != resource2) {
            String langRes1 = resource1.getLiteral().getLanguage();
            String langRes2 = resource2.getLiteral().getLanguage();

            if (langRes1 != null && langRes2 != null) {
                return langRes1.equals(langRes2);
            }
            else if (langRes1 == null && langRes2 == null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setVocabRepository(VocabRepository vocabRepository) {
        super.setVocabRepository(vocabRepository);
        resourceLabelsCollector.setVocabRepository(vocabRepository);
    }
}
