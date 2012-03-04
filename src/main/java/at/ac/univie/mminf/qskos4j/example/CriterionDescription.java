package at.ac.univie.mminf.qskos4j.example;

enum CriterionDescription {

	TOTAL_CONCEPTS("c", "All Concepts", "getInvolvedConcepts"),
	LOOSE_CONCEPTS("lc", "Loose Concepts", "findLooseConcepts");
	
	private String id, name, qSkosMethodName;
	
	CriterionDescription(String id, String name, String qSkosMethodName) {
		this.id = id;
		this.name = name;
		this.qSkosMethodName = qSkosMethodName;
	}
	
	String getId() {
		return id;
	}
	
	String getName() {
		return name;
	}
}
