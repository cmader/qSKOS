package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;

import java.util.Collection;
import java.util.HashSet;

/**
 * Finds concept schemes without top concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_Top_Concepts">Omitted Top Concepts</a>
 * ).
 */
public class OmittedTopConcepts extends Issue<CollectionReport<Resource>> {

    private ConceptSchemes conceptSchemes;

    public OmittedTopConcepts(ConceptSchemes conceptSchemes) {
        super(conceptSchemes.getVocabRepository(),
              "otc",
              "Omitted Top Concepts",
              "Finds skos:ConceptSchemes without top concepts",
              IssueType.ANALYTICAL
        );
        this.conceptSchemes = conceptSchemes;
    }

    @Override
    protected CollectionReport<Resource> invoke() throws OpenRDFException {
        RepositoryConnection connection = vocabRepository.getRepository().getConnection();
        Collection<Resource> csWithOmittedTopConcepts = new HashSet<Resource>();

        for (Resource conceptScheme : conceptSchemes.getReport().getData()) {

            BooleanQuery hasTopConceptQuery = connection.prepareBooleanQuery(
                    QueryLanguage.SPARQL,
                    createConceptSchemeWithoutTopConceptQuery(conceptScheme));

            if (!hasTopConceptQuery.evaluate()) {
                csWithOmittedTopConcepts.add(conceptScheme);
            }
        }

        return new CollectionReport<Resource>(csWithOmittedTopConcepts);
    }

    private String createConceptSchemeWithoutTopConceptQuery(Value conceptScheme) {
        return SparqlPrefix.SKOS+
                "ASK {" +
                "<"+conceptScheme.stringValue()+"> (skos:hasTopConcept|^skos:topConceptOf)+ ?topConcept"+
                "}";
    }
}
