package at.ac.univie.mminf.qskos4j.criteria;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Identifies all skos:Concepts in the repository passed to the constructor
 * @author christian
 */
public class ConceptFinder extends Criterion {
	
	private final Logger logger = LoggerFactory.getLogger(ConceptFinder.class);
	private Collection<URI> involvedConcepts, authoritativeConcepts;
	private String authResourceIdentifier;
	
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
	
	private void extractAuthoritativeConceptsFromInvolved() 
	{		
		if (authResourceIdentifier == null || authResourceIdentifier.isEmpty())
		{
			guessAuthoritativeResourceIdentifier();
		}
		
		authoritativeConcepts = new HashSet<URI>();
		
		for (URI concept : involvedConcepts) {
			String lowerCaseUriValue = concept.toString().toLowerCase();
			
			if (lowerCaseUriValue.contains(authResourceIdentifier.toLowerCase())) 
			{
				authoritativeConcepts.add(concept);
			}
		}
	}
	
	private void guessAuthoritativeResourceIdentifier() {
		HostNameOccurrencies hostNameOccurencies = new HostNameOccurrencies();
		
		Iterator<URI> resourcesListIt = new MonitoredIterator<URI>(
			involvedConcepts,
			progressMonitor,
			"guessing publishing host");
		
		while (resourcesListIt.hasNext()) {
			try {
				URL url = new URL(resourcesListIt.next().stringValue());
				hostNameOccurencies.put(url.getHost());
			}
			catch (MalformedURLException e) {
				// ignore this exception and continue with next concept
			}
		}
		
		authResourceIdentifier = hostNameOccurencies.getMostOftenOccuringHostName();
		logger.info("Guessed external resource identifier: '" +authResourceIdentifier+ "'");
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
			return sortedEntries.first().getKey();
		}
	}
}
