package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import java.util.Collection;

/**
 * Created by christian on 26.09.14.
 */
public class AmbiguousNotationWithinOneResource extends AmbiguousNotationResult {

    private Resource authConcept;
    private Collection<Literal> notationsForConcept;

    public AmbiguousNotationWithinOneResource(Resource authConcept, Collection<Literal> notationsForConcept) {
        this.authConcept = authConcept;
        this.notationsForConcept = notationsForConcept;
    }


}
