package at.ac.univie.mminf.qskos4j.issues.labelissues.util;

import org.openrdf.model.Resource;

import java.util.HashSet;
import java.util.Set;


public class LabelConflict {

    private Set<LabeledResource> conflicts;

    public LabelConflict() {
        conflicts = new HashSet<LabeledResource>();
    }

	public LabelConflict(Set<LabeledResource> conflicts)
	{
        this.conflicts = conflicts;
	}

    public Set<Resource> getAffectedResources() {
        Set<Resource> affectedResources = new HashSet<Resource>();

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
        return obj instanceof LabeledResource && conflicts.equals(obj);
    }
}
