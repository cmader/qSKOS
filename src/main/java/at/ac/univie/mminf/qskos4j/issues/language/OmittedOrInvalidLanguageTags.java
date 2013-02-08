package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.util.language.LanguageTag;
import org.openrdf.model.util.language.LanguageTagSyntaxException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
* Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_or_Invalid_Language_Tags">Omitted or Invalid Language Tags</a>.
*/
public class OmittedOrInvalidLanguageTags extends Issue<MissingLangTagReport> {

	private Map<Resource, Collection<Literal>> missingLangTags;

    public OmittedOrInvalidLanguageTags(VocabRepository vocabRepo) {
        super(vocabRepo,
              "oilt",
              "Omitted or Invalid Language Tags",
              "Finds omitted or invalid language tags of text literals",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected MissingLangTagReport invoke() throws OpenRDFException {
		if (missingLangTags == null) {
			TupleQueryResult result = vocabRepository.query(createMissingLangTagQuery());
			generateMissingLangTagMap(result);
		}
		return new MissingLangTagReport(missingLangTags);
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
