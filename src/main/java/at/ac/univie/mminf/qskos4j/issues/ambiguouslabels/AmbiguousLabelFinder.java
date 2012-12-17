package at.ac.univie.mminf.qskos4j.issues.ambiguouslabels;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import at.ac.univie.mminf.qskos4j.result.custom.ResourceLabelsResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class AmbiguousLabelFinder extends Issue {

	private Map<URI, Collection<String>> ambiguouslyLabeledResources, conceptLanguages;
	
	public AmbiguousLabelFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public ResourceLabelsResult findAmbiguouslyPreflabeledResources() throws OpenRDFException
	{
        ambiguouslyLabeledResources = new HashMap<URI, Collection<String>>();

		TupleQueryResult result = vocabRepository.query(createNotUniquePrefLabelsQuery());
        createAmbigPrefLabelMap(result);
		
		return new ResourceLabelsResult(ambiguouslyLabeledResources);
	}
	
	private String createNotUniquePrefLabelsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?resource ?prefLabel WHERE "+
			"{" +
				"?resource skos:prefLabel ?prefLabel . " +
				"{"+
					"SELECT ?resource WHERE " +
					"{"+
						"?resource skos:prefLabel ?somePrefLabel . " +
					"}" +
					"GROUP BY ?resource "+
					"HAVING (COUNT(?somePrefLabel) > 1)" +
				"}" +
			"}";
	}
	
	private void createAmbigPrefLabelMap(TupleQueryResult result)
		throws QueryEvaluationException 
	{
        conceptLanguages = new HashMap<URI, Collection<String>>();

		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI resource = (URI) queryResult.getValue("resource");
			Literal prefLabel = (Literal) queryResult.getValue("prefLabel"); 

			String lang = prefLabel.getLanguage();
			lang = lang == null ? "" : lang;

            if (isAmbiguous(resource, lang)) {
                addToAmbiguouslyLabeledConceptsMap(resource, prefLabel);
            }
		}
	}

    private boolean isAmbiguous(URI concept, String langTag) {
        Collection<String> languages = conceptLanguages.get(concept);
        if (languages != null && languages.contains(langTag)) {
            return true;
        }
        else {
            if (languages == null) languages = new HashSet<String>();
            languages.add(langTag);
            conceptLanguages.put(concept, languages);
            return false;
        }
    }
	
	private void addToAmbiguouslyLabeledConceptsMap(URI concept, Literal prefLabel)
	{		
		Collection<String> ambiguousLabels = ambiguouslyLabeledResources.get(concept);
		if (ambiguousLabels == null) {
			ambiguousLabels = new HashSet<String>();
			ambiguouslyLabeledResources.put(concept, ambiguousLabels);
		}
		ambiguousLabels.add(prefLabel.stringValue());
	}
	
	public ResourceLabelsResult findDisjointLabelsViolations() throws OpenRDFException
	{
		ambiguouslyLabeledResources = new HashMap<URI, Collection<String>>();
		
		TupleQueryResult result = vocabRepository.query(createNotDisjointLabelsQuery());
		addNotDisjointLabelsToAmbiguouslyLabeledResourceMap(result);
		
		return new ResourceLabelsResult(ambiguouslyLabeledResources);
	}
	
	private String createNotDisjointLabelsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT DISTINCT ?resource ?prefLabel ?altLabel ?hiddenLabel "+
			"{" +
			"{?resource skos:prefLabel ?prefLabel ." +
			"?resource skos:altLabel ?altLabel . " +
			"OPTIONAL {?resource skos:hiddenLabel ?hiddenLabel}} UNION "+
			"{?resource skos:prefLabel ?prefLabel ." +
			"?resource skos:hiddenLabel ?hiddenLabel ." +
			"OPTIONAL {?resource skos:altLabel ?altLabel}} UNION "+
			"{?resource skos:altLabel ?altLabel . " +
			"?resource skos:hiddenLabel ?hiddenLabel ." +
			"OPTIONAL {?resource skos:prefLabel ?prefLabel}}" +
			"}" +
			"ORDER BY ?resource";
	}
	
	private void addNotDisjointLabelsToAmbiguouslyLabeledResourceMap(TupleQueryResult result)
		throws QueryEvaluationException
	{
		NonDisjointLabelFinder ndlf = null;
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			
			URI resource = (URI) queryResult.getValue("resource");
			if (ndlf == null || !ndlf.getConcept().equals(resource)) {
				ndlf = new NonDisjointLabelFinder(resource);
			}
			
			Literal prefLabel = (Literal) queryResult.getValue("prefLabel");
			Literal altLabel = (Literal) queryResult.getValue("altLabel");
			Literal hiddenLabel = (Literal) queryResult.getValue("hiddenLabel");
			
			if (!ndlf.addPrefLabelAndCheckIfDisjoint(prefLabel)) {
				addToAmbiguouslyLabeledConceptsMap(resource, prefLabel);
			}
			if (!ndlf.addAltLabelAndCheckIfDisjoint(altLabel)) {
				addToAmbiguouslyLabeledConceptsMap(resource, altLabel);
			}
			if (!ndlf.addHiddenLabelAndCheckIfDisjoint(hiddenLabel)) {
				addToAmbiguouslyLabeledConceptsMap(resource, hiddenLabel);
			}
		}
	}

}
