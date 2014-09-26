package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

/**
 * Created by christian on 26.09.14.
 */
public class AmbiguousNotationMultipleResources extends AmbiguousNotationResult {

    private Resource authConcept, conflictingResource;
    private Literal notationLiteral;

    public AmbiguousNotationMultipleResources(Resource authConcept, Resource conflictingResource, Literal notationLiteral) {
        this.authConcept = authConcept;
        this.conflictingResource = conflictingResource;
        this.notationLiteral = notationLiteral;
    }

    @Override
    public int hashCode() {
        return authConcept.hashCode() + conflictingResource.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AmbiguousNotationMultipleResources) {
            AmbiguousNotationMultipleResources other = (AmbiguousNotationMultipleResources) obj;

            return (authConcept.equals(other.authConcept) && conflictingResource.equals(other.conflictingResource)) ||
                   (authConcept.equals(other.conflictingResource) && conflictingResource.equals(other.authConcept));
        }
        return false;

    }
}
