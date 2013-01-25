package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Identifies all skos:Concepts in the repository passed to the constructor
 * @author christian
 */
public class ConceptFinder extends Issue {
	
	private final Logger logger = LoggerFactory.getLogger(ConceptFinder.class);
	private Collection<URI> involvedConcepts, authoritativeConcepts;
	private String authResourceIdentifier;
	
	/**
	 * @param vocabRepository The repository that should be scanned for concepts
	 */
	public ConceptFinder(
		VocabRepository vocabRepository)
	{
		super(vocabRepository);
	}
	
	public CollectionResult<URI> findOrphanConcepts()
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
	
	public CollectionResult<URI> findAuthoritativeConcepts(
		String authResourceIdentifier) throws OpenRDFException
	{
		if (authoritativeConcepts == null) {
			this.authResourceIdentifier = authResourceIdentifier;
			
			if (involvedConcepts == null) {
				findInvolvedConcepts();
			}
			
			extractAuthoritativeConceptsFromInvolved();
		}
		
		return new CollectionResult<URI>(authoritativeConcepts);
	}
	


	public String getAuthoritativeResourceIdentifier() {
		return authResourceIdentifier;
	}
	
	@SuppressWarnings("serial")
	private class HostNameOccurrencies extends HashMap<String, Integer>
	{
		HostNameOccurrencies() {
			super();
		}
		
		void put(String hostname) {
			Integer occurencies = get(hostname);
			put(hostname, occurencies == null ? 1 : ++occurencies);
		}
		
		String getMostOftenOccuringHostName() {
			SortedSet<Map.Entry<String, Integer>> sortedEntries = new TreeSet<Map.Entry<String, Integer>>(
				new Comparator<Map.Entry<String, Integer>>() 
				{
					@Override 
					public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				}
			);
			
			sortedEntries.addAll(entrySet());
			if (!sortedEntries.isEmpty()) {
				return sortedEntries.first().getKey();
			}
			
			return "";
		}
	}
}
