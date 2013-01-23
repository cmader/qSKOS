package at.ac.univie.mminf.qskos4j.issues.labelissues;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabeledResource;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InconsistentPrefLabelFinder extends Issue {

    private Map<Resource, LabelConflict> ambigPrefLabels;
    private ResourceLabelsCollector resourceLabelsCollector;

	public InconsistentPrefLabelFinder(VocabRepository vocabRepository, ResourceLabelsCollector resourceLabelsCollector) {
		super(vocabRepository);
        this.resourceLabelsCollector = resourceLabelsCollector;
	}
	
	public CollectionResult<LabelConflict> findInconsistentPrefLabels() throws OpenRDFException
	{
        if (ambigPrefLabels == null) {
            Map<Resource, Collection<LabeledResource>> prefLabelsByUri = orderPrefLabelsByResource();
            extractPrefLabelConflicts(prefLabelsByUri);
        }

		return new CollectionResult<LabelConflict>(ambigPrefLabels.values());
	}

    private Map<Resource, Collection<LabeledResource>> orderPrefLabelsByResource() {
        Map<Resource, Collection<LabeledResource>> prefLabelsByResource = new HashMap<Resource, Collection<LabeledResource>>();

        for (LabeledResource labeledResource : resourceLabelsCollector.getLabeledResources()) {
            if (labeledResource.getLabelType() != LabelType.PREF_LABEL) continue;

            Collection<LabeledResource> labeledResourcesOfUri = prefLabelsByResource.get(labeledResource.getResource());
            if (labeledResourcesOfUri == null) {
                labeledResourcesOfUri = new ArrayList<LabeledResource>();
                prefLabelsByResource.put(labeledResource.getResource(), labeledResourcesOfUri);
            }

            labeledResourcesOfUri.add(labeledResource);
        }

        return prefLabelsByResource;
    }

    private void extractPrefLabelConflicts(Map<Resource, Collection<LabeledResource>> prefLabelsByResource) {
        ambigPrefLabels = new HashMap<Resource, LabelConflict>();

        for (Map.Entry<Resource, Collection<LabeledResource>> entry : prefLabelsByResource.entrySet()) {
            LabelConflict labelConflict = findPrefLabelConflict(entry.getValue());
            if (labelConflict != null) {
                ambigPrefLabels.put(entry.getKey(), labelConflict);
            }
        }
    }

    private LabelConflict findPrefLabelConflict(Collection<LabeledResource> labeledResources) {
        LabelConflict labelConflict = null;

        for (LabeledResource labeledResource : labeledResources) {
            for (LabeledResource otherLabeledResource : labeledResources) {
                if (hasPrefLabelConflict(labeledResource, otherLabeledResource)) {
                    if (labelConflict == null) labelConflict = new LabelConflict();
                    labelConflict.add(labeledResource);
                    labelConflict.add(otherLabeledResource);
                }
            }
        }

        return labelConflict;
    }

    private boolean hasPrefLabelConflict(LabeledResource resource1, LabeledResource resource2) {
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

}
