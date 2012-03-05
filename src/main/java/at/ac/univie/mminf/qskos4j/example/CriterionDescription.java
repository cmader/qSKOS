package at.ac.univie.mminf.qskos4j.example;

enum CriterionDescription {

	TOTAL_CONCEPTS("c", 
		"All Concepts", 
		"Finds all SKOS concepts involved in the vocabulary", 
		"getInvolvedConcepts"),
	AUTHORITATIVE_CONCPETS("ac", 
		"Authoritative Concepts", 
		"Finds all authoritative concepts in the vocabulary", 
		"getAuthoritativeConcepts"),
	
	LOOSE_CONCEPTS("lc", 
		"Loose Concepts", 
		"Finds all loose concepts as defined in https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC1_Relative_Number_of_Loose_Concepts", 
		"findLooseConcepts"),
	WEAKLY_CONNECTED_COMPONENTS("wcc", 
		"Weakly Connected Components", 
		"Finds all weakly connected components as defined in https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC2_Weakly_Connected_Components", 
		"findComponents"),
	HIERARCHICAL_CYCLES("hc", 
		"Hierarchical Cycles", 
		"Finds all cycle containing components, for additional information see https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC3_Cyclic_Hierarchical_Relations", 
		"findHierarchicalCycles"),
	
	REDUNDANT_ASSOCIATIVE_RELATIONS("rar", 
		"Redundant Associative Relations", 
		"Two concepts are sibling, but also connected by an associative relation", 
		"findRedundantAssociativeRelations"),
	HIERARCHICALLY_AND_ASSOCIATIVELY_RELATED_CONCEPTS("harc",
		"Hierarchically and Associatively Related Concepts",
		"Concepts that are both hierarchically and associatively connected",
		"findAmbiguousRelations"),
	UNIDIRECTIONALLY_RELATED_CONCEPTS("urc",
		"Unidirectionally Related Concepts",
		"Concepts not including reciprocal relations",	
		"findOmittedInverseRelations"),
	SOLELY_TRANSITIVELY_RELATED_CONCEPTS("strc",
		"Solely Transitively Related Concepts",
		"Concepts only related by skos:broaderTransitive or skos:narrowerTransitive, see https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC_ST4_Solely_Transitively_Related_Concepts",
		"findSolitaryTransitiveRelations"),	
	
	CONCEPT_EXT_LINK_AVG("cela",
		"Concept External Link Average",
		"Average count of each concept's links to external resources",
		"findExternalResources"),
	HTTP_URI_SCHEME_VIOLATION("husv",
		"HTTP URI Scheme Violation",
		"Finds triple subjects that are no HTTP URIs",
		"findNonHttpResources"),
	LINK_TARGET_AVAILABILITY("lta",
		"Link Target Availability",
		"Checks dereferencability of all links",
		"checkResourceAvailability"),
	AVG_CONCEPT_INDEGREE("aci",
		"Average Concept In-degree",
		"Average number of other datasets referencing concepts of the vocabulary",
		"analyzeConceptsRank"),
	
	ASS_VS_HIER_RELATION_CLASHES("ahrc",
		"Associative vs. Hierarchical Relation Clashes",
		"Covers condition S27 from the SKOS reference document",
		"findAssociativeVsHierarchicalClashes"),
	EXACT_VS_ASS_MAPPING_CLASHES("eamc",
		"Exact vs. Associative and Hierarchical Mapping Clashes",
		"Covers condition S46 from the SKOS reference document",	
		"findExactVsAssociativeMappingClashes"),
		
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
