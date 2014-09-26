package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by christian on 26.09.14.
 */
public class AmbiguousNotationWithinOneResource extends AmbiguousNotation {

    private Resource authConcept;
    private Collection<Literal> notationsForConcept;

    public AmbiguousNotationWithinOneResource(Resource authConcept, Collection<Literal> notationsForConcept) {
        this.authConcept = authConcept;
        this.notationsForConcept = notationsForConcept;
    }

    @Override
    public String toString() {
        String ret = "Resource: " +authConcept.stringValue()+ ", Notations: ";

        Iterator<Literal> notationsIt = notationsForConcept.iterator();
        while (notationsIt.hasNext()) {
            ret += notationsIt.next().stringValue() + (notationsIt.hasNext() ? ", " : "");
        }

        return ret;
    }
}
