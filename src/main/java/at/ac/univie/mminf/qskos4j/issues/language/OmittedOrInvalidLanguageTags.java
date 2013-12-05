package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.*;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.*;

/**
* Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_or_Invalid_Language_Tags">Omitted or Invalid Language Tags</a>.
*/
public class OmittedOrInvalidLanguageTags extends Issue<OmittedOrInvalidLanguageTagsResult> {

    private Map<Resource, Collection<String>> untaggedLiterals;
    private Map<String, Boolean> checkedLanguageTags;

    public OmittedOrInvalidLanguageTags() {
        super("oilt",
              "Omitted or Invalid Language Tags",
              "Finds omitted or invalid language tags of text literals",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#omitted-or-invalid-language-tags")
        );
    }

    @Override
    protected OmittedOrInvalidLanguageTagsResult invoke() throws OpenRDFException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createMissingLangTagQuery())
            .evaluate();

        findAffectedStatements(result);
        return new OmittedOrInvalidLanguageTagsResult(untaggedLiterals);
	}

    private String createMissingLangTagQuery() throws OpenRDFException
    {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL +" "+ SparqlPrefix.RDFS +
			"SELECT ?s ?p ?literal "+
			"WHERE {" +
				"?s ?p ?literal . " +

                "{?p rdfs:subPropertyOf rdfs:label}" +
                "UNION" +
                "{?p rdfs:subPropertyOf skos:note}" +

				"FILTER isLiteral(?literal) " +
			"}";
	}

    private void findAffectedStatements(TupleQueryResult result)
            throws QueryEvaluationException
    {
        checkedLanguageTags = new HashMap<String, Boolean>();
        affectedStatements = new ArrayList<>();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Resource subject = (Resource) queryResult.getValue("s");
            URI predicate = (URI) queryResult.getValue("p");
            Literal literal = (Literal) queryResult.getValue("literal");

            if (literal.getDatatype() == null) {
                String langTag = literal.getLanguage();
                if (langTag == null || !isValidLangTag(langTag)) {
                    affectedStatements.add(new StatementImpl(subject, predicate, literal));
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

}
