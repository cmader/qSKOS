package at.ac.univie.mminf.qskos4j.util.measureinvocation;

public enum MeasureDescription {

	// Labeling and Documentation Issues
	UNDOCUMENTED_CONCEPTS("uc",
		"Undocumented Concepts",
		"Finds concepts that don't use any SKOS documentation properties",
		"findUndocumentedConcepts"),

				
	// Structural Issues
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
	RELATION_CLASHES("rc",
		"Relation Clashes",
		"Covers condition S27 from the SKOS reference document (Associative vs. Hierarchical Relation Clashes)",
		"findRelationClashes"),
	MAPPING_CLASHES("mc",
		"Mapping Clashes",
		"Covers condition S46 from the SKOS reference document (Exact vs. Associative and Hierarchical Mapping Clashes)",
		"findMappingClashes"),



		
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
