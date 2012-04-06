package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

class SkosLabel {

	public static enum LabelType {PREF_LABEL, ALT_LABEL, HIDDEN_LABEL}
	
	private URI concept;
	private Literal literal;
	private LabelType labelType;
	
	SkosLabel(URI concept, Literal literal, LabelType labelType) 
	{
		this.concept = concept;
		this.literal = literal;
		this.labelType = labelType;
	}

	public Literal getLiteral() {
		return literal;
	}

	public LabelType getLabelType() {
		return labelType;
	}
	
	public URI getConcept() {
		return concept;
	}
	
	@Override
	public String toString() {
		return concept+ ": \""+ literal.getLabel() +"\"@"+ literal.getLanguage() +"("+ labelType +")";
	}
	
}
