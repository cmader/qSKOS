package at.ac.univie.mminf.qskos4j.util.graph;

import at.ac.univie.mminf.qskos4j.util.Tuple;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;

public class AssociativeRelation {

    private Tuple<Resource> concepts;
    private URI uri;

    public AssociativeRelation(Resource src, Resource dst, URI uri) {
        concepts = new Tuple<Resource>(src, dst);
        this.uri = uri;
    }

    public Tuple<Resource> getConcepts() {
        return concepts;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AssociativeRelation) {
            AssociativeRelation other = (AssociativeRelation) obj;
            return other.concepts.equals(concepts) && other.uri.equals(uri);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return concepts.hashCode() + uri.hashCode();
    }
}
