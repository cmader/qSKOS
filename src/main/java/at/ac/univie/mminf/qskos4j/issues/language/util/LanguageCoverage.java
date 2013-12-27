package at.ac.univie.mminf.qskos4j.issues.language.util;

import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LanguageCoverage {

    private final Logger logger = LoggerFactory.getLogger(LanguageCoverage.class);

    private Map<Resource, Collection<String>> languageCoverage;

    public Map<Resource, Collection<String>> findLanguageCoverage(
        Collection<Resource> resources,
        IProgressMonitor progressMonitor,
        RepositoryConnection repCon) throws OpenRDFException
    {
        languageCoverage = new HashMap<Resource, Collection<String>>();

        Iterator<Resource> it = new MonitoredIterator<Resource>(resources, progressMonitor);
        while (it.hasNext()) {
            Resource concept = it.next();

            try {
                TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createLanguageLiteralQuery(concept));
                addToLanguageCoverageMap(concept, query.evaluate());
            }
            catch (OpenRDFException e) {
                logger.error("Error finding languages for concept '" +concept+ "'");
            }
        }

        return languageCoverage;
    }

    private String createLanguageLiteralQuery(Value concept) {
        return "SELECT DISTINCT ?literal "+
                "WHERE {" +
                "<"+concept+"> ?p ?literal ."+
                "FILTER isLiteral(?literal) "+
                "FILTER langMatches(lang(?literal), \"*\")" +
                "}";
    }

    private void addToLanguageCoverageMap(Resource concept, TupleQueryResult result) throws QueryEvaluationException {
        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Literal literal = (Literal) queryResult.getValue("literal");

            String language = literal.getLanguage();
            addToLanguageCoverageMap(concept, language);
        }
    }

    private void addToLanguageCoverageMap(Resource value, String langTag) {
        Collection<String> langTags = languageCoverage.get(value);
        if (langTags == null) {
            langTags = new HashSet<String>();
            languageCoverage.put(value, langTags);
        }
        langTags.add(langTag);
    }

}
