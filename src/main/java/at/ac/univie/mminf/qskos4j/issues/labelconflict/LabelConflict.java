package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import java.util.HashSet;
import java.util.Set;


public class LabelConflict {

    private Literal literal;
    private Set<LabeledResource> labeledConcepts;

	LabelConflict(Literal literal, Set<LabeledResource> labeledConcepts)
	{
        this.literal = literal;
        this.labeledConcepts = labeledConcepts;
	}

    public Set<URI> getAffectedResources() {
        Set<URI> affectedConcepts = new HashSet<URI>();

        for (LabeledResource labeledConcept : labeledConcepts) {
            affectedConcepts.add(labeledConcept.getResource());
        }
        return affectedConcepts;
    }

	@Override
	public String toString() {
		return literal.toString() +": "+ labeledConcepts.toString();
	}
}
