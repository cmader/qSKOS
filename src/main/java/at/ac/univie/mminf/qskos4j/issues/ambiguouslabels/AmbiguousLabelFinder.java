package at.ac.univie.mminf.qskos4j.issues.ambiguouslabels;

import java.util.*;

import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabelConflict;
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
    private Set<LabelConflict> ambigPrefLabels, nonDisjointLabels;
	
	public AmbiguousLabelFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public CollectionResult<LabelConflict> findAmbiguouslyPreflabeledResources() throws OpenRDFException
	{
        if (ambigPrefLabels == null) {
            createResourceLabelsMap();
            processPrefLabels();
        }

		return new CollectionResult<LabelConflict>(ambigPrefLabels);
	}

    private void processPrefLabels() {
        Map<URI, Collection<LabeledResource>> prefLabelsByUri = orderPrefLabelsByUri();
        //findAmbigPrefLabels(prefLabelsByUri);
    }

    private Map<URI, Collection<LabeledResource>> orderPrefLabelsByUri() {
        ambigPrefLabels = new HashSet<LabelConflict>();
        Map<URI, Collection<LabeledResource>> prefLabelsByUri = new HashMap<URI, Collection<LabeledResource>>();

        for (LabeledResource labeledResource : labeledResources) {
            if (labeledResource.getLabelType() != LabeledResource.LabelType.PREF_LABEL) continue;

            Literal prefLabel = labeledResource.getLiteral();

            Collection<LabeledResource> labeledResourcesOfUri = prefLabelsByUri.get(labeledResource.getResource());
            if (labeledResourcesOfUri == null) {
                labeledResourcesOfUri = new HashSet<LabeledResource>();
            }

            if (labeledResourcesOfUri.contains(labeledResource)) {
                //ambigPrefLabels.add(new LabelConflict(prefLabel, ))
            }




            labeledResourcesOfUri.add(labeledResource);
        }

        return prefLabelsByUri;
    }

	public CollectionResult<LabelConflict> findDisjointLabelsViolations() throws OpenRDFException
	{
        if (nonDisjointLabels == null) {
            createResourceLabelsMap();
            //findNonDisjointLabels();
        }

        return new CollectionResult<LabelConflict>(nonDisjointLabels);
    }

    private void createResourceLabelsMap() throws OpenRDFException {
        if (labeledResources == null) {
            labeledResources = new HashSet<LabeledResource>();
		
		    TupleQueryResult result = vocabRepository.query(createResourceLabelsQuery());
            addResultToLabels(result);
        }
	}

	private String createResourceLabelsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT DISTINCT ?resource ?prefLabel ?altLabel ?hiddenLabel "+
			"{" +
			"{?resource skos:prefLabel ?prefLabel ." +
            "OPTIONAL {?resource skos:altLabel ?altLabel} "+
			"OPTIONAL {?resource skos:hiddenLabel ?hiddenLabel}} UNION "+

            "{?resource skos:altLabel ?altLabel ." +
            "OPTIONAL {?resource skos:prefLabel ?prefLabel} "+
            "OPTIONAL {?resource skos:hiddenLabel ?hiddenLabel}} UNION "+

            "{?resource skos:hiddenLabel ?hiddenLabel ." +
            "OPTIONAL {?resource skos:prefLabel ?prefLabel} "+
            "OPTIONAL {?resource skos:altLabel ?altLabel}}"+

			"}" +
			"ORDER BY ?resource";
	}

    private void addResultToLabels(TupleQueryResult result) throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();

			URI resource = (URI) queryResult.getValue("resource");

            try {
                Literal prefLabel = (Literal) queryResult.getValue("prefLabel");
                Literal altLabel = (Literal) queryResult.getValue("altLabel");
                Literal hiddenLabel = (Literal) queryResult.getValue("hiddenLabel");

                if (prefLabel != null) {
                    labeledResources.add(new LabeledResource(resource, prefLabel, LabeledResource.LabelType.PREF_LABEL));
                }

                if (altLabel != null) {
                    labeledResources.add(new LabeledResource(resource, altLabel, LabeledResource.LabelType.ALT_LABEL));
                }

                if (hiddenLabel != null) {
                    labeledResources.add(new LabeledResource(resource, hiddenLabel, LabeledResource.LabelType.HIDDEN_LABEL));
                }
            }
            catch (ClassCastException e) {
                logger.info("literal label expected for resource " +resource.toString()+ ", " +e.toString());
            }
		}
    }

}
