package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.URI;

import java.util.HashSet;
import java.util.Set;


public class LabelConflict {

    private Set<LabeledResource> conflicts;

    public LabelConflict() {
        conflicts = new HashSet<LabeledResource>();
    }

	LabelConflict(Set<LabeledResource> conflicts)
	{
        this.conflicts = conflicts;
	}

    public Set<URI> getAffectedResources() {
        Set<URI> affectedResources = new HashSet<URI>();

        for (LabeledResource labeledConcept : conflicts) {
            affectedResources.add(labeledConcept.getResource());
        }
        return affectedResources;
    }

    public void add(LabeledResource labeledResource) {
        conflicts.add(labeledResource);
    }

	@Override
	public String toString() {
        return conflicts.toString();
	}

    @Override
    public int hashCode() {
        return conflicts.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return conflicts.equals(obj);
    }
}
