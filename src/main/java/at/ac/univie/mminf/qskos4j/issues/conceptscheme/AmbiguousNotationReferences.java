package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.util.AmbiguousNotationMultipleResources;
import at.ac.univie.mminf.qskos4j.issues.labels.util.AmbiguousNotationResult;
import at.ac.univie.mminf.qskos4j.issues.labels.util.AmbiguousNotationWithinOneResource;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.SKOS;
import org.openrdf.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian on 25.09.14.
 */
public class AmbiguousNotationReferences extends Issue<CollectionResult<AmbiguousNotationResult>> {

    private AuthoritativeConcepts authoritativeConcepts;

    public AmbiguousNotationReferences(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
                "anr",
                "Ambiguous Notation References",
                "Finds concepts within the same concept scheme with identical notations",
                IssueType.ANALYTICAL,
                new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#Ambiguous_Notation_References"));
        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected CollectionResult<AmbiguousNotationResult> invoke() throws OpenRDFException {
        Set<AmbiguousNotationResult> problematicRelations = new HashSet<>();

        for (Resource authConcept : authoritativeConcepts.getResult().getData()) {
            RepositoryResult<Statement> conceptsWithNotation = repCon.getStatements(authConcept, SKOS.NOTATION, null, false);

            Collection<Literal> notationsForConcept = new ArrayList<>();
            while (conceptsWithNotation.hasNext()) {
                Literal notationLiteral = (Literal) conceptsWithNotation.next().getObject();
                notationsForConcept.add(notationLiteral);

                RepositoryResult<Statement> conceptsWithSameNotation = repCon.getStatements(null, SKOS.NOTATION, notationLiteral, false);
                while (conceptsWithSameNotation.hasNext()) {
                    Resource conflictingResource = conceptsWithSameNotation.next().getSubject();

                    if (conflictingResource.equals(authConcept)) continue;

                    if (ConceptSchemeUtil.inSameConceptScheme(authConcept, conflictingResource, repCon) ||
                        ConceptSchemeUtil.inNoConceptScheme(authConcept, conflictingResource, repCon))
                    {
                        problematicRelations.add(new AmbiguousNotationMultipleResources(authConcept, conflictingResource, notationLiteral));
                    }
                }
            }

            if (notationsForConcept.size() > 1) {
                problematicRelations.add(new AmbiguousNotationWithinOneResource(authConcept, notationsForConcept));
            }

        }

        return new CollectionResult<>(problematicRelations);
    }

}
