package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

class LabeledConcept {

	public static enum LabelType {PREF_LABEL, ALT_LABEL, HIDDEN_LABEL}
	
	private Literal literal;
	private LabelType labelType;
    private URI concept;
	
	LabeledConcept(URI concept, Literal literal, LabelType labelType)
	{
        this.concept = concept;
		this.literal = literal;
		this.labelType = labelType;
	}

	public Literal getLiteral() {
		return literal;
	}

    public URI getConcept() {
        return concept;
    }

	@Override
	public String toString() {
		return concept +" ("+ literal +", "+ labelType +")";
	}

    @Override
    public int hashCode() {
        return concept.hashCode() * literal.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabeledConcept)) {
            return false;
        }

        LabeledConcept other = (LabeledConcept) obj;
        return concept.equals(other.concept) && literal.equals(other.literal);
    }
}
