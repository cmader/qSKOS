package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;

/**
 * Created by christian on 26.09.14.
 */
class ConceptSchemeUtil {

    public static boolean inSameConceptScheme(Resource concept, Resource otherConcept, RepositoryConnection repCon)
        throws RDF4JException
    {
        return repCon.prepareBooleanQuery(QueryLanguage.SPARQL, createInSchemeQuery(concept, otherConcept)).evaluate();
    }

    private static String createInSchemeQuery(Resource... concepts) {
        String query = SparqlPrefix.SKOS + "ASK {";

        for (Resource concept : concepts) {
            query += "<" +concept.stringValue()+ "> skos:inScheme ?conceptScheme .";
        }

        return query + "}";
    }

    public static boolean inNoConceptScheme(Resource concept, Resource otherConcept, RepositoryConnection repCon)
        throws RDF4JException
    {
        boolean conceptInScheme = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, createInSchemeQuery(concept)).evaluate();
        boolean otherConceptInScheme = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, createInSchemeQuery(otherConcept)).evaluate();

        return !conceptInScheme || !otherConceptInScheme;
    }

}
