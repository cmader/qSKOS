package at.ac.univie.mminf.qskos4j.issues.labels.util;

public enum LabelType {

    PREF_LABEL("skos:prefLabel"),
    ALT_LABEL("skos:altLabel"),
    HIDDEN_LABEL("skos:hiddenLabel");

    private String skosProperty;

    LabelType(String skosProperty) {
        this.skosProperty = skosProperty;
    }

    public String getSkosProperty() {
        return skosProperty;
    }
}
