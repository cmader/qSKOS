package at.ac.univie.mminf.qskos4j.util.measureinvocation;

public enum MeasureDescription {

	// Statistics
	SEMANTIC_RELATIONS_COUNT("sr",
		"Semantic Relations Count",
		"Counts the number of relations between concepts (skos:semanticRelation and subproperties thereof)",
		"findSemanticRelationsCount",
		IssueType.STATISTICS),
	AGGREGATION_RELATIONS_COUNT("ar",
		"Aggregation Relations Count",
		"Counts the statements relating resources to ConceptSchemes or Collections",
		"findAggregationRelations",
		IssueType.STATISTICS),
	CONCEPT_SCHEME("cs",
		"Concept Schemes",
		"Finds the involved ConceptSchemes",
		"findConceptSchemes",
		IssueType.STATISTICS),
	COLLECTION_COUNT("cc",
		"Collection Count",
		"Counts the involved Collections",
		"findCollectionCount",
		IssueType.STATISTICS),
    HTTP_URI_COUNT("huc",
        "HTTP URI Count",
        "Counts the total number of HTTP URIs",
        "findAllHttpUriCount",
        IssueType.STATISTICS),
	
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
	OVERLAPPING_LABELS("ol",
		"Overlapping Labels",
		"Finds concepts with similar (identical) labels",
		"findOverlappingLabels"),
				
	// Structural Issues		
	ORPHAN_CONCEPTS("oc", 
		"Orphan Concepts", 
		"Finds all orphan concepts, i.e. those not having semantic relationships to other concepts", 
		"findOrphanConcepts"),
	DISCONNECTED_CONCEPT_CLUSTERS("dcc",
		"Disconnected Concept Clusters",
		"Finds sets of concepts that are isolated from the rest of the vocabulary",
		"findClusters"),
	CYCLIC_HIERARCHICAL_RELATIONS("chr", 
		"Cyclic Hierarchical Relations", 
		"Finds all hierarchy cycle containing components", 
		"findHierarchicalCycles"),
	VALUELESS_ASSOCIATIVE_RELATIONS("var", 
		"Valueless Associative Relations", 
		"Two concepts are sibling, but also connected by an associative relation", 
		"findValuelessAssociativeRelations"),
	SOLELY_TRANSITIVELY_RELATED_CONCEPTS("strc",
		"Solely Transitively Related Concepts",
		"Concepts only related by skos:broaderTransitive or skos:narrowerTransitive",
		"findSolelyTransitivelyRelatedConcepts"),
	OMITTED_TOP_CONCEPTS("otc",
		"Omitted Top Concepts",
		"Finds skos:ConceptSchemes without top concepts",
		"findOmittedTopConcepts"),
	TOP_CONCEPTS_HAVING_BROADER_CONCEPTS("tchbc",
		"Top Concepts Having Broader Concepts",
		"Finds top concepts internal to the vocabulary hierarchy tree",
		"findTopConceptsHavingBroaderConcepts"),	
		
	// Linked Data Specific Issues
	MISSING_INLINKS("mil",
		"Missing In-Links",
		"Uses the sindice index to find concepts that aren't referenced by other datasets on the Web",
		"findMissingInLinks"),	
	MISSING_OUTLINKS("mol",
		"Missing Out-Links",
		"Finds concepts that are not linked to other vocabularies on the Web",
		"findMissingOutLinks"),
	LINK_TARGET_AVAILABILITY("bl",
		"Broken Links",
		"Checks dereferencability of all links",
		"findBrokenLinks"),
	UNDEFINED_SKOS_RESOURCES("usr",
		"Undefined SKOS Resources",
		"Finds 'invented' new terms within the SKOS namespace or deprecated properties",	
		"findUndefinedSkosResources"),
		
	// Other Issues	
	UNIDIRECTIONALLY_RELATED_CONCEPTS("urc",
		"Unidirectionally Related Concepts",
		"Concepts not including reciprocal relations",	
		"findUnidirectionallyRelatedConcepts"),		
	HTTP_URI_SCHEME_VIOLATION("husv",
		"HTTP URI Scheme Violation",
		"Finds triple subjects that are no HTTP URIs",
		"findNonHttpResources"),
	RELATION_CLASHES("rc",
		"Relation Clashes",
		"Covers condition S27 from the SKOS reference document (Associative vs. Hierarchical Relation Clashes)",
		"findRelationClashes"),
	MAPPING_CLASHES("mc",
		"Mapping Clashes",
		"Covers condition S46 from the SKOS reference document (Exact vs. Associative and Hierarchical Mapping Clashes)",
		"findMappingClashes"),
	
	INCONSISTENT_PREFERRED_LABELS("ipl",
		"Inconsistent Preferred Labels",
		"Finds resources with more then one prefLabel per language",
		"findInconsistentPrefLabels"),
	DISJOINT_LABELS_VIOLATION("dlv",
		"Disjoint Labels Violation",
		"Finds resources with identical entries for different label types",
		"findDisjointLabelsViolations");
		
	public enum IssueType {STATISTICS, ISSUE}
	
	private String id, name, description, qSkosMethodName;
	private IssueType type = IssueType.ISSUE;
	
	MeasureDescription(String id, String name, String description, String qSkosMethodName) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.qSkosMethodName = qSkosMethodName;		
	}
	
	MeasureDescription(String id, String name, String description, String qSkosMethodName, IssueType type) {
		this(id, name, description, qSkosMethodName);
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	String getQSkosMethodName() {
		return qSkosMethodName;
	}
	
	public IssueType getType() {
		return type;
	}
	
	public static MeasureDescription findById(String id)
		throws UnsupportedMeasureIdException
	{
		for (MeasureDescription measureDesc : values()) {
			if (measureDesc.id.equals(id)) {
				return measureDesc;
			}
		}
		
		throw new UnsupportedMeasureIdException(id);
	}
	
}
