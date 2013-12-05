package at.ac.univie.mminf.qskos4j.util.graph;

import at.ac.univie.mminf.qskos4j.util.Tuple;
import org.openrdf.model.Resource;

import java.util.Collection;

public class UnidirectionalRelation {

    private Tuple<Resource> resources;
    private Collection<String> inverseRelations;

    public UnidirectionalRelation(Tuple<Resource> resources, Collection<String> inverseRelations) {
        this.resources = resources;
        this.inverseRelations = inverseRelations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnidirectionalRelation) {
            UnidirectionalRelation other = (UnidirectionalRelation) obj;
            return resources.equals(other.resources) && inverseRelations.equals(other.inverseRelations);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resources.hashCode() + inverseRelations.hashCode();
    }
}