package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;

import java.util.Collection;
import java.util.HashSet;

/**
 * Finds concept schemes without top concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_Top_Concepts">Omitted Top Concepts</a>
 * ).
 */
public class OmittedTopConcepts extends Issue<CollectionResult<Resource>> {

    private ConceptSchemes conceptSchemes;

    public OmittedTopConcepts(ConceptSchemes conceptSchemes) {
        super(new IssueDescriptor.Builder(
            "otc",
            "Omitted Top Concepts",
            "Finds skos:ConceptSchemes that don't have top concepts defined",
            IssueDescriptor.IssueType.ANALYTICAL)
                .weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#omitted-top-concepts")
                .dependentIssue(conceptSchemes)
                .build());

        this.conceptSchemes = conceptSchemes;
    }

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
        Collection<Resource> csWithOmittedTopConcepts = new HashSet<>();

        for (Resource conceptScheme : conceptSchemes.getResult().getData()) {

            BooleanQuery hasTopConceptQuery = repCon.prepareBooleanQuery(
                    QueryLanguage.SPARQL,
                    createConceptSchemeWithoutTopConceptQuery(conceptScheme));

            if (!hasTopConceptQuery.evaluate()) {
                csWithOmittedTopConcepts.add(conceptScheme);
            }
        }

        return new CollectionResult<>(csWithOmittedTopConcepts);
    }

    private String createConceptSchemeWithoutTopConceptQuery(Value conceptScheme) {
        return SparqlPrefix.SKOS+
                "ASK {" +
                "<"+conceptScheme.stringValue()+"> (skos:hasTopConcept|^skos:topConceptOf)+ ?topConcept"+
                "}";
    }
}
