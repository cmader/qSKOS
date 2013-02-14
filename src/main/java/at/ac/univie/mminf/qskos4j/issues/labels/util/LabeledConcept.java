package at.ac.univie.mminf.qskos4j.issues.labels.util;

import at.ac.univie.mminf.qskos4j.report.HtmlRenderable;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class LabeledConcept implements HtmlRenderable {

	private Literal literal;
	private LabelType labelType;
    private Value concept;
	
	public LabeledConcept(Value concept, Literal literal, LabelType labelType)
	{
        this.concept = concept;
		this.literal = literal;
		this.labelType = labelType;
	}

	public Literal getLiteral() {
		return literal;
	}

    public Value getConcept() {
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
        return concept.hashCode() * literal.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabeledConcept)) {
            return false;
        }

        LabeledConcept other = (LabeledConcept) obj;
        return concept.equals(other.concept) && literal.equals(other.literal) && labelType.equals(other.labelType);
    }

    @Override
    public String toHtml() {
        String htmlString = "<div class='" +this.getClass().getName()+ "'>";

        if (concept instanceof URI) {
            htmlString += "<a href='" +concept.stringValue()+ "'>" +concept.stringValue()+ "</a>";
        }
        else {
            htmlString += concept.stringValue();
        }
        htmlString += " ("+ literal +", "+ labelType +")";

        htmlString += "</div>";
        return htmlString;
    }
}
