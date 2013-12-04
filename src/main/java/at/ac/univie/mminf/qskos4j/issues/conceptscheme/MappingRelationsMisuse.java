package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.Collection;

public class MappingRelationsMisuse extends Issue<Collection<Statement>> {

    private AuthoritativeConcepts authoritativeConcepts;

    public MappingRelationsMisuse(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
            "mri",
            "Mapping Relations Misuse",
            "Finds concepts within the same concept scheme that are related by a mapping relation",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#mapping-relations-misuse"));
        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected Collection<Statement> computeResult() throws OpenRDFException {
        Collection<Statement> problematicRelations = new ArrayList<Statement>();

        RepositoryResult<Statement> result = repCon.getStatements(
                null,
                SkosOntology.getInstance().getUri("mappingRelation"),
                null,
                true);
        while (result.hasNext()) {
            Statement st = result.next();
            Resource concept = st.getSubject();
            Resource otherConcept = (Resource) st.getObject();

            if (areAuthoritativeConcepts(concept, otherConcept) &&
               (inSameConceptScheme(concept, otherConcept) || inNoConceptScheme(concept, otherConcept)))
            {
                problematicRelations.add(st);
            }
        }

        return problematicRelations;
    }

    private boolean areAuthoritativeConcepts(Resource... concepts) throws OpenRDFException {
        for (Resource concept : concepts) {
            boolean isAuthoritativeConcept = false;
            for (Resource authoritativeConcept : authoritativeConcepts.getResult()) {
                if (concept.equals(authoritativeConcept)) isAuthoritativeConcept = true;
            }
            if (!isAuthoritativeConcept) return false;
        }

        return true;
    }

    private boolean inSameConceptScheme(Resource concept, Resource otherConcept) throws OpenRDFException {
        return repCon.prepareBooleanQuery(QueryLanguage.SPARQL, createInSchemeQuery(concept, otherConcept)).evaluate();
    }

    private boolean inNoConceptScheme(Resource concept, Resource otherConcept) throws OpenRDFException {
        boolean conceptInScheme = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, createInSchemeQuery(concept)).evaluate();
        boolean otherConceptInScheme = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, createInSchemeQuery(otherConcept)).evaluate();

        return !conceptInScheme || !otherConceptInScheme;
    }

    private String createInSchemeQuery(Resource... concepts) {
        String query = SparqlPrefix.SKOS + "ASK {";

        for (Resource concept : concepts) {
            query += "<" +concept.stringValue()+ "> skos:inScheme ?conceptScheme .";
        }

        return query + "}";
    }

    @Override
    protected Report generateReport(Collection<Statement> preparedData) {
        return new CollectionReport<Statement>(preparedData);
    }
}
