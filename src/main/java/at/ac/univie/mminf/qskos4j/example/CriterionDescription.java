package at.ac.univie.mminf.qskos4j.example;

enum CriterionDescription {

	TOTAL_CONCEPTS("c", "All Concepts", "Finds all SKOS concepts involved in the vocabulary", "getInvolvedConcepts"),
	AUTHORITATIVE_CONCPETS("ac", "Authoritative Concepts", "Finds all authoritative concepts in the vocabulary", "getAuthoritativeConcepts"),
	LOOSE_CONCEPTS("lc", "Loose Concepts", "Finds all loose concepts", "findLooseConcepts"),
	WEAKLY_CONNECTED_COMPONENTS("wcc", "Weakly Connected Components", "Finds all weakly connected components", "findComponents"),
	HIERARCHICAL_CYCLES("hc", "Hierarchical Cycles", "Finds all cycle containing components", "findHierarchicalCycles"),
	
	NULL_DESC("", "", "", "");
	
	private String id, name, description, qSkosMethodName;
	
	CriterionDescription(String id, String name, String description, String qSkosMethodName) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.qSkosMethodName = qSkosMethodName;
	}
	
	String getId() {
		return id;
	}
	
	String getName() {
		return name;
	}
	
	String getDescription() {
		return description;
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
