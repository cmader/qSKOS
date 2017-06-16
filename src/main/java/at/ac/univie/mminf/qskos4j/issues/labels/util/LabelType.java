package at.ac.univie.mminf.qskos4j.issues.labels.util;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.model.URI;

import java.util.ArrayList;
import java.util.Collection;

public enum LabelType {

    PREF_LABEL(SparqlPrefix.SKOS.getNameSpace() + "prefLabel", true, "skos:prefLabel"),
    ALT_LABEL(SparqlPrefix.SKOS.getNameSpace() + "altLabel", true, "skos:altLabel"),
    HIDDEN_LABEL(SparqlPrefix.SKOS.getNameSpace() + "hiddenLabel", true, "skos:hiddenLabel"),
    TITLE(SparqlPrefix.DCTERMS.getNameSpace() + "title", false, "dcterms:title"),
    LABEL(SparqlPrefix.RDFS.getNameSpace() + "label", false, "rdfs:label"),
    UNKNOWN(null, false, "null");

    private String predicate;
    private boolean definedInSkos;
    private String usualPrefixedDisplayUri;

    private LabelType(String predicate, boolean definedInSkos, String usualPrefixedDisplayUri) {
        this.predicate = predicate;
        this.definedInSkos = definedInSkos;
        this.usualPrefixedDisplayUri = usualPrefixedDisplayUri;
    }

    public String getPredicate() {
        return predicate;
    }

    public boolean isDefinedInSkos() {
		return definedInSkos;
	}

	public String getUsualPrefixedDisplayUri() {
		return usualPrefixedDisplayUri;
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
