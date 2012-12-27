package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

public class LabeledResource {

	public static enum LabelType {PREF_LABEL, ALT_LABEL, HIDDEN_LABEL}
	
	private Literal literal;
	private LabelType labelType;
    private URI resource;
	
	public LabeledResource(URI resource, Literal literal, LabelType labelType)
	{
        this.resource = resource;
		this.literal = literal;
		this.labelType = labelType;
	}

	public Literal getLiteral() {
		return literal;
	}

    public URI getResource() {
        return resource;
    }

    public LabelType getLabelType() {
        return labelType;
    }

	@Override
	public String toString() {
		return resource +" ("+ literal +", "+ labelType +")";
	}

    @Override
    public int hashCode() {
        return resource.hashCode() * literal.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabeledResource)) {
            return false;
        }

        LabeledResource other = (LabeledResource) obj;
        return resource.equals(other.resource) && literal.equals(other.literal) && labelType.equals(other.labelType);
    }
}
