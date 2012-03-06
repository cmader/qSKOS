package at.ac.univie.mminf.qskos4j.example;

enum CriterionDescription {

	// Statistics
	TOTAL_CONCEPTS("c", 
		"All Concepts", 
		"Finds all SKOS concepts involved in the vocabulary", 
		"getInvolvedConcepts"),
	AUTHORITATIVE_CONCEPTS("ac", 
		"Authoritative Concepts", 
		"Finds all authoritative concepts in the vocabulary", 
		"getAuthoritativeConcepts"),
	LEXICAL_RELATIONS_COUNT("lr",
		"Lexical Relations Count",
		"Counts the number of relations between all concepts and lexical labels (prefLabel, altLabel, hiddenLabel and subproperties thereof)",
		"findLexicalRelationsCount"),
	SEMANTIC_RELATIONS_COUNT("sr",
		"Semantic Relations Count",
		"Counts the number of relations between concepts (skos:semanticRelation and subproperties thereof)",
		"findSemanticRelationsCount"),
	AGGREGATION_RELATIONS_COUNT("ar",
		"Aggregation Relations Count",
		"Counts the statements relating resources to ConceptSchemes or Collections",
		"findAggregationRelations"),
	CONCEPT_SCHEME_COUNT("cs",
		"Concept Scheme Count",
		"Counts the involved ConceptSchemes",
		"findConceptSchemeCount"),
	COLLECTION_COUNT("cc",
		"Collection Count",
		"Counts the involved Collections",
		"findCollectionCount"),
	
	// Graph-based measures
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
	
	// Structure-related measures
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
	
	// Linked Data related measures
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
	
	// SKOS related measures	
	ASS_VS_HIER_RELATION_CLASHES("ahrc",
		"Associative vs. Hierarchical Relation Clashes",
		"Covers condition S27 from the SKOS reference document",
		"findAssociativeVsHierarchicalClashes"),
	EXACT_VS_ASS_MAPPING_CLASHES("eamc",
		"Exact vs. Associative and Hierarchical Mapping Clashes",
		"Covers condition S46 from the SKOS reference document",	
		"findExactVsAssociativeMappingClashes"),
	ILLEGAL_TERMS("it",
		"Illegal SKOS terms",
		"Finds 'invented' new terms within the SKOS namespace",	
		"findIllegalTerms"),
	DEPRECATED_PROP_USAGE("dpu",
		"Deprecated property usage",
		"Finds usage of properties according to appendix D of the SKOS reference",
		"findDeprecatedProperties"),
	OMITTED_TOP_CONCEPTS("otc",
		"Omitted Top Concepts",
		"Finds skos:ConceptSchemes without top concepts",
		"findConceptSchemesWithoutTopConcept"),
	TOP_CONCEPTS_HAVING_BROADER("tcb",
		"Top Concepts Having Broader Concepts",
		"Finds top concepts internal to the vocabulary hierarchy tree",
		"findTopConceptsHavingBroaderConcept"),
	
	// Labeling issues	
	MISSING_LANG_TAGS("mlt",
		"Language Tag Support",
		"Finds missing language tags of text literals",
		"findMissingLanguageTags"),
	CONCEPTS_INCOMPLETE_LANG_COVERAGE("cilc",	
		"Concepts With Incomplete Language Coverage",
		"Finds concepts lacking description in languages that are present for other concepts",
		"getIncompleteLanguageCoverage"),
	AMBIGUOUS_PREFLABELED_CONCEPTS("apl",
		"Ambiguously Preflabeled Concepts",
		"Finds concepts with more then one prefLabel per languate",	
		"findNotUniquePrefLabels"),
	NOT_DISJOINT_LABELED_CONCEPTS("ndlc",
		"Not Disjoint Labeled Concepts",
		"Finds concepts with identical entries for different label types",
		"findNotDisjointLabels"),
		
	// Other measures	
	SEM_RELATED_CONCEPTS("src",	
		"Potentially Semantically Related Concepts",
		"Finds concepts with similar (identical) labels",
		"findRelatedConcepts"),
	AVG_DOC_COVERAGE("adc",
		"Concept Documentation Coverage Ratio",
		"Calculates average use of documentation properties per concept",
		"getAverageDocumentationCoverageRatio"),
		
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
