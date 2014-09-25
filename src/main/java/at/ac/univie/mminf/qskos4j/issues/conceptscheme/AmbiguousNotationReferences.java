package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.URIImpl;

/**
 * Created by christian on 25.09.14.
 */
public class AmbiguousNotationReferences extends Issue<CollectionResult<Resource>> {

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
    protected CollectionResult<Resource> invoke() throws OpenRDFException {
        return null;
    }
}
