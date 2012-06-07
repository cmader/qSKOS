package at.ac.univie.mminf.qskos4j.issues;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.util.language.LanguageTag;
import org.openrdf.model.util.language.LanguageTagSyntaxException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.result.custom.MissingLangTagResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class LanguageTagChecker extends Issue {

	private Map<Resource, Collection<Literal>> missingLangTags;
	
	public LanguageTagChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public MissingLangTagResult findOmittedOrInvalidLanguageTags() 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		if (missingLangTags == null) {
			TupleQueryResult result = vocabRepository.query(createMissingLangTagQuery());
			generateMissingLangTagMap(result);
		}
		return new MissingLangTagResult(missingLangTags);
	}
	
	private String createMissingLangTagQuery() {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL +" "+ SparqlPrefix.RDFS +
			"SELECT ?literal ?s ?p "+
			
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"FROM NAMED <" +vocabRepository.SKOS_GRAPH_URL+ "> "+
			
			"WHERE {" +
				"?s ?p ?literal . " +
			
				"GRAPH <" +vocabRepository.SKOS_GRAPH_URL+ "> {"+
					"{?p rdfs:subPropertyOf* rdfs:label}" +
					"UNION" +
					"{?p rdfs:subPropertyOf* skos:note}" +
				"}" +
									
				"FILTER isLiteral(?literal) " +
			"}";
	}
	
	private void generateMissingLangTagMap(TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		missingLangTags = new HashMap<Resource, Collection<Literal>>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			Literal literal = (Literal) queryResult.getValue("literal");
			Resource subject = (Resource) queryResult.getValue("s");
			
			if (literal.getDatatype() == null) {
				String langTag = literal.getLanguage();			
				if (langTag == null || isInvalidLanguage(langTag)) {
					addToMissingLangTagMap(subject, literal);
				}
			}
		}
	}
	
	private boolean isInvalidLanguage(String langTag) {
		try {
			new LanguageTag(langTag);
		} 
		catch (LanguageTagSyntaxException e) {
			return true;
		}
		return false;
	}
	
	private void addToMissingLangTagMap(Resource resource, Literal literal) {
		Collection<Literal> literals = missingLangTags.get(resource);
		if (literals == null) {
			literals = new HashSet<Literal>();
			missingLangTags.put(resource, literals);
		}
		literals.add(literal);
	}
	
}
