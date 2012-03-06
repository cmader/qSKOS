package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class LanguageTagChecker extends Criterion {

	private Map<String, Set<Resource>> missingLangTags;
	
	public LanguageTagChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public Map<String, Set<Resource>> findMissingLanguageTags() 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		if (missingLangTags == null) {
			TupleQueryResult result = vocabRepository.query(createMissingLangTagQuery());
			generateMissingLangTagSet(result);
		}
		return missingLangTags;
	}
	
	private String createMissingLangTagQuery() {
		return SparqlPrefix.SKOS+
			"SELECT ?literal ?s "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"WHERE {" +
				"?s ?p ?literal . " +
				"FILTER langMatches(lang(?literal), \"\" )" +
				"FILTER NOT EXISTS {?s skos:notation ?literal}" +
			"}";
	}
	
	private void generateMissingLangTagSet(TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		missingLangTags = new HashMap<String, Set<Resource>>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			Literal literal = (Literal) queryResult.getValue("literal");
			Resource subject = (Resource) queryResult.getValue("s");
			
			if (literal.getDatatype() == null) {
				Set<Resource> concepts = missingLangTags.get(literal.stringValue());
				if (concepts == null) {
					concepts = new HashSet<Resource>();
					missingLangTags.put(literal.stringValue(), concepts);
				}
				concepts.add(subject);
			}
		}
	}

}
