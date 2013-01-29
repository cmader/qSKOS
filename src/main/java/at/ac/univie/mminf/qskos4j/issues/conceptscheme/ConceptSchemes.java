package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
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

    public ConceptSchemes(VocabRepository vocabRepo) {
        super(vocabRepo,
              "cs",
              "Concept Schemes",
              "Finds the involved ConceptSchemes",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected CollectionResult<Resource> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createConceptSchemeQuery());
        return new CollectionResult<Resource>(identifyResources(result));
    }

    private String createConceptSchemeQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +" "+ SparqlPrefix.RDF +
            "SELECT DISTINCT ?resource WHERE {" +
                "{?resource rdf:type/rdfs:subClassOf* skos:ConceptScheme}" +
                "UNION" +
                "{?resource ?hasTopConcept ?concept . ?hasTopConcept rdfs:subPropertyOf* skos:hasTopConcept}" +
                "UNION" +
                "{?concept ?topConceptOf ?resource . ?topConceptOf rdfs:subPropertyOf* skos:topConceptOf}" +
                "UNION" +
                "{?concept ?inScheme ?resource . ?inScheme rdfs:subPropertyOf* skos:inScheme}"+
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
