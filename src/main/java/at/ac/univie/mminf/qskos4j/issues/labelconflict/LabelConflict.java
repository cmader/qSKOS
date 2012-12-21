package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import java.util.HashSet;
import java.util.Set;


public class LabelConflict {

    private Literal literal;
    private Set<LabeledConcept> labeledConcepts;

	LabelConflict(Literal literal, Set<LabeledConcept> labeledConcepts)
	{
        this.literal = literal;
        this.labeledConcepts = labeledConcepts;
	}

    public Set<URI> getAffectedConcepts() {
        Set<URI> affectedConcepts = new HashSet<URI>();

        for (LabeledConcept labeledConcept : labeledConcepts) {
            affectedConcepts.add(labeledConcept.getConcept());
        }
        return affectedConcepts;
    }

	@Override
	public String toString() {
		return literal.toString() +": "+ labeledConcepts.toString();
	}
}
