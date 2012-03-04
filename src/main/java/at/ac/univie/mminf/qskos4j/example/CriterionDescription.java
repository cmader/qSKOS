package at.ac.univie.mminf.qskos4j.example;

enum CriterionDescription {

	TOTAL_CONCEPTS("c", "All Concepts", "getInvolvedConcepts"),
	LOOSE_CONCEPTS("lc", "Loose Concepts", "findLooseConcepts"),
	
	NULL_DESC("", "", "");
	
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
	
	String getQSkosMethodName() {
		return qSkosMethodName;
	}
	
	static CriterionDescription findById(String id) {
		for (CriterionDescription critDesc : values()) {
			if (critDesc.id.equals(id)) {
				return critDesc;
			}
		}
		return NULL_DESC;
	}
}
