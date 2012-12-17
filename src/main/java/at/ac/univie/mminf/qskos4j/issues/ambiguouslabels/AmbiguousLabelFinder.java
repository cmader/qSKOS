package at.ac.univie.mminf.qskos4j.issues.ambiguouslabels;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.custom.ConceptLabelsResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class AmbiguousLabelFinder extends Issue {

	private Map<URI, Collection<String>> ambiguouslyLabeledConcepts, conceptLanguages;
	
	public AmbiguousLabelFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public ConceptLabelsResult findAmbiguouslyPreflabeledConcepts() throws OpenRDFException 
	{
        ambiguouslyLabeledConcepts = new HashMap<URI, Collection<String>>();

		TupleQueryResult result = vocabRepository.query(createNotUniquePrefLabelsQuery());
        createAmbigPrefLabelMap(result);
		
		return new ConceptLabelsResult(ambiguouslyLabeledConcepts);
	}
	
	private String createNotUniquePrefLabelsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?concept ?prefLabel WHERE "+
			"{" +
				"?concept skos:prefLabel ?prefLabel . " +
				"{"+
					"SELECT ?concept WHERE " +
					"{"+
						"?concept skos:prefLabel ?somePrefLabel . " +
					"}" +
					"GROUP BY ?concept "+
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
			URI concept = (URI) queryResult.getValue("concept");
			Literal prefLabel = (Literal) queryResult.getValue("prefLabel"); 

			String lang = prefLabel.getLanguage();
			lang = lang == null ? "" : lang;

            if (isAmbiguous(concept, lang)) {
                addToAmbiguouslyLabeledConceptsMap(concept, prefLabel);
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
		Collection<String> ambiguousLabels = ambiguouslyLabeledConcepts.get(concept);
		if (ambiguousLabels == null) {
			ambiguousLabels = new HashSet<String>();
			ambiguouslyLabeledConcepts.put(concept, ambiguousLabels);
		}
		ambiguousLabels.add(prefLabel.stringValue());
	}
	
	public ConceptLabelsResult findDisjointLabelsViolations() throws OpenRDFException 
	{
		ambiguouslyLabeledConcepts = new HashMap<URI, Collection<String>>();
		
		TupleQueryResult result = vocabRepository.query(createNotDisjointLabelsQuery());
		addNotDisjointLabelsToAmbiguouslyLabeledConceptsMap(result);
		
		return new ConceptLabelsResult(ambiguouslyLabeledConcepts);
	}
	
	private String createNotDisjointLabelsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT DISTINCT ?concept ?prefLabel ?altLabel ?hiddenLabel "+
			"{" +
			"{?concept skos:prefLabel ?prefLabel ." +
			"?concept skos:altLabel ?altLabel . " +
			"OPTIONAL {?concept skos:hiddenLabel ?hiddenLabel}} UNION "+
			"{?concept skos:prefLabel ?prefLabel ." +
			"?concept skos:hiddenLabel ?hiddenLabel ." +
			"OPTIONAL {?concept skos:altLabel ?altLabel}} UNION "+
			"{?concept skos:altLabel ?altLabel . " +
			"?concept skos:hiddenLabel ?hiddenLabel ." +
			"OPTIONAL {?concept skos:prefLabel ?prefLabel}}" +
			"}" +
			"ORDER BY ?concept";
	}
	
	private void addNotDisjointLabelsToAmbiguouslyLabeledConceptsMap(
		TupleQueryResult result) 
		throws QueryEvaluationException
	{
		NonDisjointLabelFinder ndlf = null;
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			
			URI concept = (URI) queryResult.getValue("concept");
			if (ndlf == null || !ndlf.getConcept().equals(concept)) {
				ndlf = new NonDisjointLabelFinder(concept);
			}
			
			Literal prefLabel = (Literal) queryResult.getValue("prefLabel");
			Literal altLabel = (Literal) queryResult.getValue("altLabel");
			Literal hiddenLabel = (Literal) queryResult.getValue("hiddenLabel");
			
			if (!ndlf.addPrefLabelAndCheckIfDisjoint(prefLabel)) {
				addToAmbiguouslyLabeledConceptsMap(concept, prefLabel);
			}
			if (!ndlf.addAltLabelAndCheckIfDisjoint(altLabel)) {
				addToAmbiguouslyLabeledConceptsMap(concept, altLabel);
			}
			if (!ndlf.addHiddenLabelAndCheckIfDisjoint(hiddenLabel)) {
				addToAmbiguouslyLabeledConceptsMap(concept, hiddenLabel);
			}
		}
	}

}
