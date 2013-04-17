package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;

import java.util.*;

/**
* Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_or_Invalid_Language_Tags">Omitted or Invalid Language Tags</a>.
*/
public class OmittedOrInvalidLanguageTags extends Issue<Map<Resource, Collection<Literal>>> {

	private Map<Resource, Collection<Literal>> missingLangTags;
    private Map<String, Boolean> checkedLanguageTags;

    public OmittedOrInvalidLanguageTags(RepositoryConnection repCon) {
        super(repCon,
              "oilt",
              "Omitted or Invalid Language Tags",
              "Finds omitted or invalid language tags of text literals",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected Map<Resource, Collection<Literal>> prepareData() throws OpenRDFException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createMissingLangTagQuery()).evaluate();
        generateMissingLangTagMap(result);
        return missingLangTags;
	}

    @Override
    protected Report prepareReport(Map<Resource, Collection<Literal>> preparedData) {
        return new MissingLangTagReport(preparedData);
    }

    private String createMissingLangTagQuery() throws OpenRDFException
    {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL +" "+ SparqlPrefix.RDFS +
			"SELECT ?literal ?s "+
			"WHERE {" +
				"?s ?textProp ?literal . " +

				"FILTER isLiteral(?literal) " +
                createSkosTextualPropertiesFilter()+
			"}";
	}

    private String createSkosTextualPropertiesFilter() throws OpenRDFException
    {
        RepositoryConnection skosRepConn = SkosOntology.getInstance().getRepository().getConnection();
        try {
            TupleQuery skosTextPropQuery = skosRepConn.prepareTupleQuery(QueryLanguage.SPARQL, createSkosTextualPropertiesQuery());
            return TupleQueryResultUtil.getFilterForBindingName(skosTextPropQuery.evaluate(), "textProp");
        }
        finally {
            skosRepConn.close();
        }
    }

    private String createSkosTextualPropertiesQuery() {
        return SparqlPrefix.SKOS +" "+  SparqlPrefix.RDFS +
            "SELECT ?textProp WHERE {" +
                "{?textProp rdfs:subPropertyOf* rdfs:label}" +
                "UNION" +
                "{?textProp rdfs:subPropertyOf* skos:note}" +
            "}";
    }

	private void generateMissingLangTagMap(TupleQueryResult result)
		throws QueryEvaluationException 
	{
		missingLangTags = new HashMap<Resource, Collection<Literal>>();
        checkedLanguageTags = new HashMap<String, Boolean>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			Literal literal = (Literal) queryResult.getValue("literal");
			Resource subject = (Resource) queryResult.getValue("s");

            if (literal.getDatatype() == null) {
				String langTag = literal.getLanguage();			
				if (langTag == null || !isValidLangTag(langTag)) {
					addToMissingLangTagMap(subject, literal);
				}
			}
		}
	}
	
	private boolean isValidLangTag(String langTag) {
        Boolean validTag = checkedLanguageTags.get(langTag);

        if (validTag == null) {
            validTag = isSyntacticallyCorrect(langTag) && hasIsoLanguage(langTag);
            checkedLanguageTags.put(langTag, validTag);
        }

        return validTag;
    }

    private boolean isSyntacticallyCorrect(String langTag) {
        try {
            new Locale.Builder().setLanguageTag(langTag);
        }
        catch (IllformedLocaleException e) {
            return false;
        }

        return true;
    }

    private boolean hasIsoLanguage(String langTag) {
        Locale locale = new Locale.Builder().setLanguageTag(langTag).build();

        boolean hasIsoLanguage = false;
        for (String isoLanguage : Locale.getISOLanguages()) {
            if (isoLanguage.equalsIgnoreCase(locale.getLanguage())) {
                hasIsoLanguage = true;
                break;
            }
        }

        return hasIsoLanguage;
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
