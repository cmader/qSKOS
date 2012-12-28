package at.ac.univie.mminf.qskos4j.issues.labelissues;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabeledResource;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AmbiguousLabelFinder extends Issue {

    private Map<URI, LabelConflict> ambigPrefLabels;
    private ResourceLabelsCollector resourceLabelsCollector;

	public AmbiguousLabelFinder(VocabRepository vocabRepository, ResourceLabelsCollector resourceLabelsCollector) {
		super(vocabRepository);
        this.resourceLabelsCollector = resourceLabelsCollector;
	}
	
	public CollectionResult<LabelConflict> findAmbiguouslyPreflabeledResources() throws OpenRDFException
	{
        if (ambigPrefLabels == null) {
            Map<URI, Collection<LabeledResource>> prefLabelsByUri = orderPrefLabelsByUri();
            extractPrefLabelConflicts(prefLabelsByUri);
        }

		return new CollectionResult<LabelConflict>(ambigPrefLabels.values());
	}

    private Map<URI, Collection<LabeledResource>> orderPrefLabelsByUri() {
        Map<URI, Collection<LabeledResource>> prefLabelsByUri = new HashMap<URI, Collection<LabeledResource>>();

        for (LabeledResource labeledResource : resourceLabelsCollector.getLabeledResources()) {
            if (labeledResource.getLabelType() != LabelType.PREF_LABEL) continue;

            Collection<LabeledResource> labeledResourcesOfUri = prefLabelsByUri.get(labeledResource.getResource());
            if (labeledResourcesOfUri == null) {
                labeledResourcesOfUri = new ArrayList<LabeledResource>();
                prefLabelsByUri.put(labeledResource.getResource(), labeledResourcesOfUri);
            }

            labeledResourcesOfUri.add(labeledResource);
        }

        return prefLabelsByUri;
    }

    private void extractPrefLabelConflicts(Map<URI, Collection<LabeledResource>> prefLabelsByUri) {
        ambigPrefLabels = new HashMap<URI, LabelConflict>();

        for (Map.Entry<URI, Collection<LabeledResource>> entry : prefLabelsByUri.entrySet()) {
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
