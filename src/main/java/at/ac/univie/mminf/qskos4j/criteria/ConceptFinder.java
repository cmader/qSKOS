package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies all skos:Concepts in the repository passed to the constructor
 * @author christian
 */
public class ConceptFinder extends Criterion {
	
	private Set<URI> involvedConcepts, authoritativeConcepts;
	
	/**
	 * @param vocabRepository The repository that should be scanned for concepts
	 * @param onlyLooseConcepts If true, returns only concepts, that have no skos relations
	 * to other resources
	 */
	public ConceptFinder(
		VocabRepository vocabRepository)
	{
		super(vocabRepository);
	}
	
	public CollectionResult<URI> findInvolvedConcepts() 
		throws OpenRDFException 
	{
		TupleQueryResult result = vocabRepository.query(createConceptsQuery());
		Set<URI> foundConcepts = getConceptURIs(result); 
		involvedConcepts = foundConcepts;  
		
		return new CollectionResult<URI>(foundConcepts);
	}
	
	public CollectionResult<URI> findLooseConcepts()
		throws OpenRDFException
	{
		TupleQueryResult result = vocabRepository.query(createLooseConceptsQuery());
		Set<URI> connectedConcepts = getConceptURIs(result);
		
		if (involvedConcepts == null) {
			findInvolvedConcepts();
		}
		
		Set<URI> looseConcepts = new HashSet<URI>(involvedConcepts);
		looseConcepts.removeAll(connectedConcepts);

		return new CollectionResult<URI>(looseConcepts);	
	}
	
	private String createLooseConceptsQuery() {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?concept ?semanticRelation ?otherConcept WHERE" +
			"{" +
				"{?concept ?semanticRelation ?otherConcept . ?semanticRelation rdfs:subPropertyOf+ skos:semanticRelation}" +
				"UNION" +
				"{?otherConcept ?semanticRelation ?concept . ?semanticRelation rdfs:subPropertyOf+ skos:semanticRelation}" +
			"}";				
	}
	
	public CollectionResult<URI> getAuthoritativeConcepts(
		String publishingHost,
		String authoritativeUriSubstring) throws OpenRDFException
	{
		if (authoritativeConcepts == null) {
		
			if (involvedConcepts == null) {
				findInvolvedConcepts();
			}
			
			extractAuthoritativeConceptsFromInvolved(
				publishingHost,	
				authoritativeUriSubstring);
		}
		
		return new CollectionResult<URI>(authoritativeConcepts);
	}
	
	private void extractAuthoritativeConceptsFromInvolved(
		String publishingHost,	
		String authoritativeUriSubstring) 
	{		
		if ((publishingHost == null || publishingHost.isEmpty()) &&
			(authoritativeUriSubstring == null || authoritativeUriSubstring.isEmpty()))
		{
			throw new IllegalArgumentException("no publishing host and no authoritative uri substring given");
		}
		
		authoritativeConcepts = new HashSet<URI>();
		
		for (URI concept : involvedConcepts) {
			String lowerCaseUriValue = concept.toString().toLowerCase();
			
			if (publishingHost != null && lowerCaseUriValue.contains(publishingHost.toLowerCase()) ||
				authoritativeUriSubstring != null && lowerCaseUriValue.contains(authoritativeUriSubstring.toLowerCase())) 
			{
				authoritativeConcepts.add(concept);
			}
		}
	}
		
	private String createConceptsQuery() {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?concept "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"FROM NAMED <" +vocabRepository.SKOS_GRAPH_URL+ "> "+
			
			"WHERE {" +
				"{?concept rdf:type/rdfs:subClassOf* skos:Concept} UNION "+
				"{?concept skos:topConceptOf ?conceptScheme} UNION "+
				"{?conceptScheme skos:hasTopConcept ?concept} UNION "+
				
				"{"+
					"GRAPH <" +vocabRepository.SKOS_GRAPH_URL+ "> {"+
						"?semRelSubProp rdfs:subPropertyOf+ skos:semanticRelation ."+
					"}" +			
					"{" +
						"{?x ?semRelSubProp ?concept . } UNION "+
						"{?concept ?semRelSubProp ?x . } UNION " +
						"{?concept ?p ?x . ?p rdfs:subPropertyOf+ ?semRelSubProp} UNION " +
						"{?x ?p ?concept . ?p rdfs:subPropertyOf+ ?semRelSubProp}" +
					"}"+
				"}" +
			"}";
	}
	
	private Set<URI> getConceptURIs(TupleQueryResult result) throws QueryEvaluationException {
		Set<URI> ret = new HashSet<URI>();
		
		while (result.hasNext()) {
			Value concept = result.next().getValue("concept");
			
			if (concept instanceof URI) {
				ret.add((URI) concept);
			}
		}
		
		return ret;
	}
}
