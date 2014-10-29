package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

public class LabeledConcept {

	private Literal literal;
	private LabelType labelType;
    private Resource concept;
	
	public LabeledConcept(Resource concept, Literal literal, LabelType labelType)
	{
        this.concept = concept;
		this.literal = literal;
		this.labelType = labelType;
	}

	public Literal getLiteral() {
		return literal;
	}

    public Resource getConcept() {
        return concept;
    }

    public LabelType getLabelType() {
        return labelType;
    }

	@Override
	public String toString() {
		return concept +" ("+ literal +", "+ labelType +")";
	}

    @Override
    public int hashCode() {
        return concept.hashCode() + literal.hashCode() + labelType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabeledConcept)) {
            return false;
        }

        LabeledConcept other = (LabeledConcept) obj;
        return concept.equals(other.concept) && literal.equals(other.literal) && labelType.equals(other.labelType);
    }

}
