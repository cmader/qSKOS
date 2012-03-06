package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class LanguageCoverageChecker extends Criterion {

	private Map<Resource, Set<String>> languageCoverage, incompleteLanguageCoverage;
	private Set<String> distinctLanguages;
	
	public LanguageCoverageChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public Map<Resource, Set<String>> getIncompleteLanguageCoverage(
		Set<URI> concepts) throws OpenRDFException 
	{
		incompleteLanguageCoverage = new HashMap<Resource, Set<String>>();
		
		checkLanguageCoverage(concepts);
		generateIncompleteLanguageCoverageMap();
		
		return incompleteLanguageCoverage;
	}
	
	private void checkLanguageCoverage(Set<URI> concepts) throws OpenRDFException 
	{
		languageCoverage = new HashMap<Resource, Set<String>>();
		
		Iterator<URI> it = getMonitoredIterator(concepts);
		while (it.hasNext()) {
			URI concept = it.next();
			TupleQueryResult result = vocabRepository.query(createLanguageLiteralQuery(concept));
			addToLanguageCoverageMap(concept, result);
		}
	}
	
	private String createLanguageLiteralQuery(URI concept) {
		return "SELECT DISTINCT ?literal "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"WHERE {" +
			"<"+concept+"> ?p ?literal ."+
			"FILTER isLiteral(?literal) "+
			"FILTER langMatches(lang(?literal), \"*\")" +
			"}";
	}
	
	private void addToLanguageCoverageMap(URI concept, TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			Literal literal = (Literal) queryResult.getValue("literal");
			
			String language = literal.getLanguage();
			addToLanguageCoverageMap(concept, language);
			addToDistinctLanguages(language);
		}
	}
	
	private void addToLanguageCoverageMap(Resource resource, String langTag) {
		Set<String> langTags = languageCoverage.get(resource);
		if (langTags == null) {
			langTags = new HashSet<String>();
			languageCoverage.put(resource, langTags);
		}
		langTags.add(langTag);
	}
	
	private void addToDistinctLanguages(String langTag) {
		if (distinctLanguages == null) {
			distinctLanguages = new HashSet<String>();
		}
		distinctLanguages.add(langTag);
	}
	
	private void generateIncompleteLanguageCoverageMap() {
		incompleteLanguageCoverage = new HashMap<Resource, Set<String>>();
		
		for (Resource resource : languageCoverage.keySet()) {
			Set<String> coveredLanguages = languageCoverage.get(resource);
			Set<String> notCoveredLanguages = getNotCoveredLanguages(coveredLanguages);
			if (!notCoveredLanguages.isEmpty()) {
				incompleteLanguageCoverage.put(resource, notCoveredLanguages);
			}
		}
	}
	
	private Set<String> getNotCoveredLanguages(Set<String> coveredLanguages) {
		Set<String> ret = new HashSet<String>(distinctLanguages);
		ret.removeAll(coveredLanguages);
		return ret;
	}
	
}
