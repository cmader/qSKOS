package at.ac.univie.mminf.qskos4j.issues.labelconflict;

import org.openrdf.model.Literal;
import org.openrdf.model.impl.LiteralImpl;

class SimilarityLiteral extends LiteralImpl{

    SimilarityLiteral(Literal literal) {
        super();
        setLabel(literal.getLabel());
        setLanguage(literal.getLanguage());
        setDatatype(literal.getDatatype());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SimilarityLiteral)) {
            return false;
        }

        return isSimilar((SimilarityLiteral) obj);
    }

    private boolean isSimilar(SimilarityLiteral other) {
        return super.equals(other);
    }

}
