package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;

import java.util.Collection;
import java.util.HashSet;

/**
 * Finds concept schemes without top concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_Top_Concepts">Omitted Top Concepts</a>
 * ).
 */
public class OmittedTopConcepts extends Issue<Collection<Resource>> {

    private ConceptSchemes conceptSchemes;

    public OmittedTopConcepts(ConceptSchemes conceptSchemes) {
        super(conceptSchemes.getRepositoryConnection(),
              "otc",
              "Omitted Top Concepts",
              "Finds skos:ConceptSchemes without top concepts",
              IssueType.ANALYTICAL
        );
        this.conceptSchemes = conceptSchemes;
    }

    @Override
    protected Collection<Resource> prepareData() throws OpenRDFException {
        Collection<Resource> csWithOmittedTopConcepts = new HashSet<Resource>();

        for (Resource conceptScheme : conceptSchemes.getPreparedData()) {

            BooleanQuery hasTopConceptQuery = repCon.prepareBooleanQuery(
                    QueryLanguage.SPARQL,
                    createConceptSchemeWithoutTopConceptQuery(conceptScheme));

            if (!hasTopConceptQuery.evaluate()) {
                csWithOmittedTopConcepts.add(conceptScheme);
            }
        }

        return csWithOmittedTopConcepts;
    }

    @Override
    protected Report prepareReport(Collection<Resource> preparedData) {
        return new CollectionReport<Resource>(preparedData);
    }

    private String createConceptSchemeWithoutTopConceptQuery(Value conceptScheme) {
        return SparqlPrefix.SKOS+
                "ASK {" +
                "<"+conceptScheme.stringValue()+"> (skos:hasTopConcept|^skos:topConceptOf)+ ?topConcept"+
                "}";
    }
}
