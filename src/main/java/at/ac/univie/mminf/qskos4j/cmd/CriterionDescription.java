package at.ac.univie.mminf.qskos4j.cmd;

enum CriterionDescription {

	// Statistics
	TRIPLE_COUNT("tc",
		"Triple Count",
		"Counts all triples of the vocabulary",
		"getTripleCount"),
	TOTAL_CONCEPTS("c", 
		"All Concepts", 
		"Finds all SKOS concepts involved in the vocabulary", 
		"findInvolvedConcepts"),
	AUTHORITATIVE_CONCEPTS("ac", 
		"Authoritative Concepts", 
		"Finds all authoritative concepts in the vocabulary", 
		"findAuthoritativeConcepts"),
	LABELS_COUNT("l",
		"Labels Count",
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
	
	// Labeling and Documentation Issues
	OMITTED_OR_INVALID_LANG_TAGS("oilt",
		"Omitted or Invalid Language Tags",
		"Finds omitted or invalid language tags of text literals",
		"findOmittedOrInvalidLanguageTags"),
	INCOMPLETE_LANG_COVERAGE("ilc",	
		"Incomplete Language Coverage",
		"Finds concepts lacking description in languages that are present for other concepts",
		"findIncompleteLanguageCoverage"),
	UNDOCUMENTED_CONCEPTS("uc",
		"Undocumented Concepts",
		"Finds concepts that don't use any SKOS documentation properties",
		"findUndocumentedConcepts"),
	LABEL_CONFLICTS("lc",	
		"Label Conflicts",
		"Finds concepts with similar (identical) labels",
		"findLabelConflicts"),
				
	// Structural Issues		
	ORPHAN_CONCEPTS("oc", 
		"Orphan Concepts", 
		"Finds all orphan concepts, i.e. those not having semantic relationships to other concepts", 
		"findLooseConcepts"),
	WEAKLY_CONNECTED_COMPONENTS("wcc", 
		"Weakly Connected Components", 
		"Finds all weakly connected components as defined in https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC2_Weakly_Connected_Components", 
		"findComponents"),
	CYCLIC_HIERARCHICAL_RELATIONS("chr", 
		"Hierarchical Cycles", 
		"Finds all cycle containing components, for additional information see https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC3_Cyclic_Hierarchical_Relations", 
		"findHierarchicalCycles"),
	VALUELESS_ASSOCIATIVE_RELATIONS("var", 
		"Redundant Associative Relations", 
		"Two concepts are sibling, but also connected by an associative relation", 
		"findValuelessAssociativeRelations"),
	SOLELY_TRANSITIVELY_RELATED_CONCEPTS("strc",
		"Solely Transitively Related Concepts",
		"Concepts only related by skos:broaderTransitive or skos:narrowerTransitive, see https://github.com/cmader/qSKOS/wiki/Quality-Criteria-for-SKOS-Vocabularies#wiki-VQC_ST4_Solely_Transitively_Related_Concepts",
		"findSolelyTransitivelyRelatedConcepts"),
	OMITTED_TOP_CONCEPTS("otc",
		"Omitted Top Concepts",
		"Finds skos:ConceptSchemes without top concepts",
		"findOmittedTopConcepts"),
	TOP_CONCEPTS_HAVING_BROADER_CONCEPTS("tchbc",
		"Top Concepts Having Broader Concepts",
		"Finds top concepts internal to the vocabulary hierarchy tree",
		"findTopConceptsHavingBroaderConcepts"),	
		
	/*	
	HIERARCHICALLY_AND_ASSOCIATIVELY_RELATED_CONCEPTS("harc",
		"Hierarchically and Associatively Related Concepts",
		"Concepts that are both hierarchically and associatively connected",
		"findAmbiguousRelations"),
	UNIDIRECTIONALLY_RELATED_CONCEPTS("urc",
		"Unidirectionally Related Concepts",
		"Concepts not including reciprocal relations",	
		"findOmittedInverseRelations"),		
	
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
	
	// Labeling issues	
	AMBIGUOUS_PREFLABELED_CONCEPTS("apc",
		"Ambiguously Preflabeled Concepts",
		"Finds concepts with more then one prefLabel per languate",	
		"findNotUniquePrefLabels"),
	NOT_DISJOINT_LABELED_CONCEPTS("ndlc",
		"Not Disjoint Labeled Concepts",
		"Finds concepts with identical entries for different label types",
		"findNotDisjointLabels"),
	*/
		
	// Other measures		
		
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
