package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

/**
 * Created by christian on 15.12.14.
 */
public class LabelWithUnprintableCharacters {

    private Literal literal;
    private Resource resource;

    public LabelWithUnprintableCharacters(Literal literal, Resource resource) {
        this.literal = literal;
        this.resource = resource;
    }

}
