package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.util.*;

/**
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_or_Invalid_Language_Tags">Omitted or Invalid Language Tags</a>.
 */
public class OmittedOrInvalidLanguageTags extends Issue<OmittedOrInvalidLanguageTagsResult> {

    private Map<Resource, Collection<Literal>> missingLangTags;
    private Map<String, Boolean> checkedLanguageTags;

    public OmittedOrInvalidLanguageTags() {
        super(new IssueDescriptor.Builder("oilt",
                "Omitted or Invalid Language Tags",
                "Finds omitted or invalid language tags of text literals",
                IssueDescriptor.IssueType.ANALYTICAL)
                .weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#omitted-or-invalid-language-tags")
                .build()
        );
    }

    @Override
    protected OmittedOrInvalidLanguageTagsResult invoke() throws RDF4JException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createMissingLangTagQuery()).evaluate();
        generateMissingLangTagMap(result);

        return new OmittedOrInvalidLanguageTagsResult(missingLangTags);
    }

    private String createMissingLangTagQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL +" "+ SparqlPrefix.RDFS +
                "SELECT ?literal ?s "+
                "WHERE {" +
                "?s ?textProp ?literal . " +

                "{?textProp rdfs:subPropertyOf rdfs:label}" +
                "UNION" +
                "{?textProp rdfs:subPropertyOf skos:note}" +

                "FILTER isLiteral(?literal) " +
                "}";
    }

    private void generateMissingLangTagMap(TupleQueryResult result)
            throws QueryEvaluationException
    {
        missingLangTags = new HashMap<Resource, Collection<Literal>>();
        checkedLanguageTags = new HashMap<String, Boolean>();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Resource subject = (Resource) queryResult.getValue("s");
            Literal literal = (Literal) queryResult.getValue("literal");

            if (!SkosOntology.getInstance().isSkosResource(subject) && hasNoOrInvalidTag(literal)) {
                addToMissingLangTagMap(subject, literal);
            }
        }
    }

    private boolean hasNoOrInvalidTag(Literal literal) {
        if (literal.getLanguage().isPresent()) {
            String langTag = literal.getLanguage().orElse(null);
            return langTag == null || !isValidLangTag(langTag);
        } else {
        	return true;
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
