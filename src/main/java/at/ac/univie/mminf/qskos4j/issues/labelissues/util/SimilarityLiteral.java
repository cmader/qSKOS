package at.ac.univie.mminf.qskos4j.issues.labelissues.util;

import org.openrdf.model.Literal;
import org.openrdf.model.impl.LiteralImpl;

public class SimilarityLiteral extends LiteralImpl{

    public SimilarityLiteral(Literal literal) {
        super();
        setLabel(literal.getLabel());
        setLanguage(literal.getLanguage());
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
