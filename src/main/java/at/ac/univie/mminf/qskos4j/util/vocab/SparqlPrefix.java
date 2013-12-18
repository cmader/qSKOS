package at.ac.univie.mminf.qskos4j.util.vocab;


public enum SparqlPrefix {
	SKOS("skos", "http://www.w3.org/2004/02/skos/core#"),
	SKOSXL("skosxl", "http://www.w3.org/2008/05/skos-xl#"),
    DC("dc", "http://purl.org/dc/elements/1.1/"),
    DCTERMS("dcterms", "http://purl.org/dc/terms/"),
	RDF("rdf", org.openrdf.model.vocabulary.RDF.NAMESPACE),
	RDFS("rdfs", org.openrdf.model.vocabulary.RDFS.NAMESPACE);

	private String abbrv, nameSpace;
	
	private SparqlPrefix(String abbrv, String nameSpace) {
		this.abbrv = abbrv;
		this.nameSpace = nameSpace;
	}
	
	@Override
	public String toString() {
		return "PREFIX " +abbrv+ ":<" +nameSpace+ ">";
	}
	
	public String getNameSpace() {
		return nameSpace;
	}
}
