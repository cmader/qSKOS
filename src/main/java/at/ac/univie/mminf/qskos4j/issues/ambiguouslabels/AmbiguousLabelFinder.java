package at.ac.univie.mminf.qskos4j.issues.ambiguouslabels;

import java.text.BreakIterator;
import java.util.*;

import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabelType;
import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabeledResource;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbiguousLabelFinder extends Issue {

    private final Logger logger = LoggerFactory.getLogger(AmbiguousLabelFinder.class);

    private Collection<LabeledResource> labeledResources;
    private Map<URI, LabelConflict> ambigPrefLabels;
    private Map<Literal, LabelConflict> nonDisjointLabels;
	
	public AmbiguousLabelFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public CollectionResult<LabelConflict> findAmbiguouslyPreflabeledResources() throws OpenRDFException
	{
        if (ambigPrefLabels == null) {
            createResourceLabelsMap();
            findAmbigPrefLabels();
        }

		return new CollectionResult<LabelConflict>(ambigPrefLabels.values());
	}

    private void findAmbigPrefLabels() {
        Map<URI, Collection<LabeledResource>> prefLabelsByUri = orderPrefLabelsByUri();
        extractPrefLabelConflicts(prefLabelsByUri);
    }

    private Map<URI, Collection<LabeledResource>> orderPrefLabelsByUri() {
        Map<URI, Collection<LabeledResource>> prefLabelsByUri = new HashMap<URI, Collection<LabeledResource>>();

        for (LabeledResource labeledResource : labeledResources) {
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

	public CollectionResult<LabelConflict> findDisjointLabelsViolations() throws OpenRDFException
	{
        if (nonDisjointLabels == null) {
            createResourceLabelsMap();
            findNonDisjointLabels();
        }

        return new CollectionResult<LabelConflict>(nonDisjointLabels.values());
    }

    private void createResourceLabelsMap() throws OpenRDFException {
        if (labeledResources == null) {
            labeledResources = new HashSet<LabeledResource>();

            for (LabelType labelType : LabelType.values()) {
                TupleQueryResult result = vocabRepository.query(createLabelQuery(labelType));
                addResultToLabels(labelType, result);
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
			URI resource = (URI) queryResult.getValue("resource");

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

    private void findNonDisjointLabels() {
        Map<Literal, Collection<LabeledResource>> resourcesByLabel = orderResourcesByLabel();
        extractNonDisjointConflicts(resourcesByLabel);
    }

    private Map<Literal, Collection<LabeledResource>> orderResourcesByLabel() {
        Map<Literal, Collection<LabeledResource>> resourcesByLabel = new HashMap<Literal, Collection<LabeledResource>>();

        for (LabeledResource labeledResource : labeledResources) {
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
        if (resource1.getResource() == resource2.getResource() && resource1.getLabelType() != resource2.getLabelType())
            return true;

        return false;
    }
}
