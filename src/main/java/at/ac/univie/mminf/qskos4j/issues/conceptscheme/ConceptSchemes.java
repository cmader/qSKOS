package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:23
 *
 * Finds the number of SKOS <a href="http://www.w3.org/TR/skos-reference/#schemes">ConceptSchemes</a>.
 */
public class ConceptSchemes extends Issue<CollectionResult<Resource>> {

    private final Logger logger = LoggerFactory.getLogger(ConceptSchemes.class);

    public ConceptSchemes() {
        super("cs",
              "Concept Schemes",
              "Finds the involved ConceptSchemes",
              IssueType.STATISTICAL);
    }

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createConceptSchemeQuery()).evaluate();
        return new CollectionResult<Resource>(identifyResources(result));
    }

    private String createConceptSchemeQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +" "+ SparqlPrefix.RDF +
            "SELECT DISTINCT ?resource WHERE {" +
                "{?resource rdf:type skos:ConceptScheme}" +
                "UNION" +
                "{?resource ?hasTopConcept ?concept . ?hasTopConcept rdfs:subPropertyOf skos:hasTopConcept}" +
                "UNION" +
                "{?concept ?topConceptOf ?resource . ?topConceptOf rdfs:subPropertyOf skos:topConceptOf}" +
                "UNION" +
                "{?concept ?inScheme ?resource . ?inScheme rdfs:subPropertyOf skos:inScheme}"+
            "}";
    }

    private Collection<Resource> identifyResources(TupleQueryResult result) throws QueryEvaluationException
    {
        Collection<Resource> allResources = new HashSet<Resource>();

        while (result.hasNext()) {
            Value conceptScheme = result.next().getValue("resource");

            try {
                allResources.add((Resource) conceptScheme);
            }
            catch (ClassCastException e) {
                logger.error("Resource expected for conceptscheme " +conceptScheme.toString()+ ", " +e.toString());
            }
        }

        return allResources;
    }
}
