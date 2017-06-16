package at.ac.univie.mminf.qskos4j.issues.labels.util;


import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by christian on 26.09.14.
 */
public class AmbiguousNotationMultipleResources extends AmbiguousNotation {

    private Literal notation;
    private Collection<Resource> conflictingResources;

    public AmbiguousNotationMultipleResources(Literal notation, Collection<Resource> conflictingResources) {
        this.notation = notation;
        this.conflictingResources = conflictingResources;
    }

    @Override
    public String toString() {
        String result = "Notation: " +notation.stringValue()+ ", conflicting resources: ";
        Iterator<Resource> resourceIt = conflictingResources.iterator();
        while (resourceIt.hasNext()) {
            result += resourceIt.next().stringValue() + (resourceIt.hasNext() ? ", " : "");
        }
        return result;
    }
}
