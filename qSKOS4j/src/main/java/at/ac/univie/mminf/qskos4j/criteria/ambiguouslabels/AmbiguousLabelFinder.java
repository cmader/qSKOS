package at.ac.univie.mminf.qskos4j.criteria.ambiguouslabels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.criteria.Criterion;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class AmbiguousLabelFinder extends Criterion {

	private Map<URI, Set<String>> ambiguouslyLabeledConcepts;
	
	public AmbiguousLabelFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public Map<URI, Set<String>> findNotUniquePrefLabels() 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		ambiguouslyLabeledConcepts = new HashMap<URI, Set<String>>();
		
		TupleQueryResult result = queryRepository(createNotUniquePrefLabelsQuery());
		addPrefLabelsToAmbiguouslyLabeledConceptsMap(result);
		
		return ambiguouslyLabeledConcepts;
	}
	
	private String createNotUniquePrefLabelsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?concept ?prefLabel "+
			"{?concept skos:prefLabel ?prefLabel . "+
				"{"+
					"SELECT ?concept "+
					"{?concept skos:prefLabel ?prefLabel .} "+
					"GROUP BY ?concept "+
					"HAVING (COUNT(?prefLabel) > 1)"+ 
				"}"+
			"}";
	}
	
	private void addPrefLabelsToAmbiguouslyLabeledConceptsMap(TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		URI prevConcept = null;
		String prevLang = "";
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI concept = (URI) queryResult.getValue("concept");
			Literal prefLabel = (Literal) queryResult.getValue("prefLabel"); 
			String lang = prefLabel.getLanguage();
			lang = lang == null ? "" : lang;
			
			if (prevConcept != null && concept.equals(prevConcept) && lang.equals(prevLang)) {
				addToAmbiguouslyLabeledConceptsMap(concept, prefLabel);
			}
			
			prevConcept = concept;
			prevLang = lang;
		}
	}
	
	private void addToAmbiguouslyLabeledConceptsMap(URI concept, Literal prefLabel)
	{		
		Set<String> ambiguousLabels = ambiguouslyLabeledConcepts.get(concept);
		if (ambiguousLabels == null) {
			ambiguousLabels = new HashSet<String>();
			ambiguouslyLabeledConcepts.put(concept, ambiguousLabels);
		}
		ambiguousLabels.add(prefLabel.stringValue());
	}
	
	public Map<URI, Set<String>> findNotDisjointLabels() 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		ambiguouslyLabeledConcepts = new HashMap<URI, Set<String>>();
		
		TupleQueryResult result = queryRepository(createNotDisjointLabelsQuery());
		addNotDisjointLabelsToAmbiguouslyLabeledConceptsMap(result);
		
		return ambiguouslyLabeledConcepts;
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
