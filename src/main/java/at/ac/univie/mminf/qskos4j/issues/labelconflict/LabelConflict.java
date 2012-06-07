package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.issues.labelconflict.SkosLabel.LabelType;


public class LabelConflict {

	private URI concept1, concept2;
	private Literal label1, label2;
	private LabelType label1Type, label2Type;
	
	LabelConflict(
		URI concept1, 
		URI concept2, 
		Literal label1,
		Literal label2,
		LabelType label1Type,
		LabelType label2Type)
	{
		this.concept1 = concept1;
		this.concept2 = concept2;
		this.label1 = label1;
		this.label2 = label2;
		this.label1Type = label1Type;
		this.label2Type = label2Type;
	}

	public URI getConcept1() {
		return concept1;
	}

	public URI getConcept2() {
		return concept2;
	}

	public Literal getLabel1() {
		return label1;
	}

	public Literal getLabel2() {
		return label2;
	}

	public LabelType getLabel1Type() {
		return label1Type;
	}

	public LabelType getLabel2Type() {
		return label2Type;
	}
		
	@Override
	public String toString() {
		return concept1 +"("+label1+") <-> "+ concept2 +"("+label2+")";
	}
}
