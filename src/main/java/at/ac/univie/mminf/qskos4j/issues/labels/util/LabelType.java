package at.ac.univie.mminf.qskos4j.issues.labels.util;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.model.URI;

import java.util.ArrayList;
import java.util.Collection;

public enum LabelType {

    PREF_LABEL(SparqlPrefix.SKOS.getNameSpace() + "prefLabel", true),
    ALT_LABEL(SparqlPrefix.SKOS.getNameSpace() + "altLabel", true),
    HIDDEN_LABEL(SparqlPrefix.SKOS.getNameSpace() + "hiddenLabel", true),
    TITLE(SparqlPrefix.DCTERMS.getNameSpace() + "title"),
    LABEL(SparqlPrefix.RDFS.getNameSpace() + "label"),
    UNKNOWN(null);

    private String predicate;
    private boolean definedInSkos;

    private LabelType(String predicate) {
        this(predicate, false);
    }

    private LabelType(String predicate, boolean definedInSkos) {
        this.predicate = predicate;
        this.definedInSkos = definedInSkos;
    }

    public String getPredicate() {
        return predicate;
    }

    public static LabelType getFromUri(URI uri) {
        for (LabelType labelType : values()) {
            if (uri.stringValue().contains(labelType.predicate)) return labelType;
        }
        return UNKNOWN;
    }

    public static Collection<LabelType> getSkosLabels() {
        Collection<LabelType> skosLabels = new ArrayList<LabelType>();

        for (LabelType labelType : values()) {
            if (labelType.definedInSkos) skosLabels.add(labelType);
        }

        return skosLabels;
    }

}
