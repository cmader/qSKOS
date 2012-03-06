package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.UriCollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies all skos:Concepts in the repository passed to the constructor
 * @author christian
 */
public class ConceptFinder extends Criterion {
	
	private boolean onlyLooseConcepts;
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
	
	public UriCollectionResult getInvolvedConcepts(boolean onlyLooseConcepts) 
		throws OpenRDFException 
	{
		this.onlyLooseConcepts = onlyLooseConcepts;
		
		TupleQueryResult result = queryConcepts();
		Set<URI> foundConcepts = getConceptURIs(result); 
		
		if (!onlyLooseConcepts) {
			involvedConcepts = foundConcepts;  
		}
		
		return new UriCollectionResult(foundConcepts);
	}
	
	public Set<URI> getAuthoritativeConcepts(
		String publishingHost,
		String authoritativeUriSubstring) throws OpenRDFException
	{
		if (authoritativeConcepts == null) {
		
			if (involvedConcepts == null) {
				getInvolvedConcepts(false);
			}
			
			extractAuthoritativeConceptsFromInvolved(
				publishingHost,	
				authoritativeUriSubstring);
		}
		
		return authoritativeConcepts;
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
	
	private TupleQueryResult queryConcepts() throws OpenRDFException 
	{
		String query = createConceptsQuery();
		return vocabRepository.query(query);
	}
	
	private String createConceptsQuery() {
		String query = SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?concept "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"FROM NAMED <" +vocabRepository.SKOS_GRAPH_URL+ "> "+
			
			"WHERE {" +
				"{?concept rdf:type/rdfs:subClassOf* skos:Concept} UNION "+
				"{?concept skos:topConceptOf ?conceptScheme} UNION "+
				"{?conceptScheme skos:hasTopConcept ?concept} UNION "+
				
				"{"+
					"GRAPH <" +vocabRepository.SKOS_GRAPH_URL+ "> {"+
						"?semRelSubProp rdfs:subPropertyOf{1,3} skos:semanticRelation ."+
					"}" +
					"{" +
						"{?x ?semRelSubProp ?concept . } UNION "+
						"{?concept ?semRelSubProp ?x . } UNION " +
						"{?concept ?p ?x . ?p rdfs:subPropertyOf+ ?semRelSubProp} UNION " +
						"{?x ?p ?concept . ?p rdfs:subPropertyOf+ ?semRelSubProp}" +
					"}"+
				"}";
		
		if (onlyLooseConcepts) {
			query += "FILTER NOT EXISTS "+
				"{" +
					"GRAPH <" +vocabRepository.SKOS_GRAPH_URL+ "> {"+
						"?skosRelation rdfs:isDefinedBy <http://www.w3.org/2004/02/skos/core> . "+
					"}" +
					"{" +					
						"{?concept ?skosRelation ?resource . FILTER isIRI(?resource) } UNION "+
						"{?resource ?skosRelation ?concept . FILTER isIRI(?resource) } UNION "+
						"{?concept ?p ?resource . ?p rdfs:subPropertyOf+ ?skosRelation. FILTER isIRI(?resource) } UNION "+
						"{?resource ?p ?concept . ?p rdfs:subPropertyOf+ ?skosRelation. FILTER isIRI(?resource) }" +
					"}"+
				"}";
		}
		query += "}";
		
		return query;
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
