package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
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
public class ConceptSchemes extends Issue<Collection<Resource>> {

    private final Logger logger = LoggerFactory.getLogger(ConceptSchemes.class);

    public ConceptSchemes(RepositoryConnection repCon) {
        super(repCon,
              "cs",
              "Concept Schemes",
              "Finds the involved ConceptSchemes",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected Collection<Resource> prepareData() throws OpenRDFException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createConceptSchemeQuery()).evaluate();
        return identifyResources(result);
    }

    @Override
    protected Report prepareReport(Collection<Resource> preparedData) {
        return new CollectionReport<Resource>(preparedData);
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
                logger.info("resource expected for conceptscheme " +conceptScheme.toString()+ ", " +e.toString());
            }
        }

        return allResources;
    }
}
