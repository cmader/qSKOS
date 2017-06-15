package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.impl.LiteralImpl;

public class SimilarityLiteral extends LiteralImpl{

    public SimilarityLiteral(Literal literal) {
        super(literal.getLabel().toUpperCase());
        if(literal.getLanguage().isPresent()) {
        	setLanguage(literal.getLanguage().get());
        }
        setDatatype(literal.getDatatype());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimilarityLiteral && isSimilar((SimilarityLiteral) obj);
    }

    private boolean isSimilar(SimilarityLiteral other) {
        return super.equals(other);
    }

}
