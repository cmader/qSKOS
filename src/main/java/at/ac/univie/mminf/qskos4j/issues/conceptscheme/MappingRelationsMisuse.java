package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.Collection;

public class MappingRelationsMisuse extends Issue<CollectionResult<Statement>> {

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
    protected CollectionResult<Statement> invoke() throws OpenRDFException {
        Collection<Statement> problematicRelations = new ArrayList<>();

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
               (ConceptSchemeUtil.inSameConceptScheme(concept, otherConcept, repCon) ||
                ConceptSchemeUtil.inNoConceptScheme(concept, otherConcept, repCon)))
            {
                problematicRelations.add(st);
            }
        }

        return new CollectionResult<>(problematicRelations);
    }

    private boolean areAuthoritativeConcepts(Resource... concepts) throws OpenRDFException {
        for (Resource concept : concepts) {
            boolean isAuthoritativeConcept = false;
            for (Resource authoritativeConcept : authoritativeConcepts.getResult().getData()) {
                if (concept.equals(authoritativeConcept)) isAuthoritativeConcept = true;
            }
            if (!isAuthoritativeConcept) return false;
        }

        return true;
    }

}
