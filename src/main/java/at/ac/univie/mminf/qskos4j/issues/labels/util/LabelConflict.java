package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Resource;

import java.util.HashSet;
import java.util.Set;


public class LabelConflict {

    private Set<LabeledConcept> conflicts;

    public LabelConflict() {
        conflicts = new HashSet<LabeledConcept>();
    }

	public LabelConflict(Set<LabeledConcept> conflicts)
	{
        this.conflicts = conflicts;
	}

    public Set<Resource> getAffectedResources() {
        Set<Resource> affectedResources = new HashSet<Resource>();

        for (LabeledConcept labeledConcept : conflicts) {
            affectedResources.add(labeledConcept.getConcept());
        }
        return affectedResources;
    }

    public void add(LabeledConcept labeledResource) {
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
        return obj instanceof LabeledConcept && conflicts.equals(obj);
    }
}
