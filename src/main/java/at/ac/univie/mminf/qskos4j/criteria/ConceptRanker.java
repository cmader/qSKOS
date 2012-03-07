package at.ac.univie.mminf.qskos4j.criteria;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.result.custom.AvgConceptIndegreeResult;
import at.ac.univie.mminf.qskos4j.util.RandomSubSet;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class ConceptRanker extends Criterion {

	private final Logger logger = LoggerFactory.getLogger(ConceptRanker.class);
	
	private Collection<URI> authoritativeConcepts;
	private Set<SPARQLRepository> sparqlEndpoints;
	private Map<URI, Set<URI>> conceptsRank = new HashMap<URI, Set<URI>>();
	
	public ConceptRanker(
		VocabRepository vocabRepository,
		Set<String> sparqlEndpoints) 
	{
		super(vocabRepository);
		createSparqlRepositories(sparqlEndpoints);
	}
	
	private void createSparqlRepositories(Set<String> sparqlEndpoints) {
		this.sparqlEndpoints = new HashSet<SPARQLRepository>();
		
		if (sparqlEndpoints == null || sparqlEndpoints.isEmpty()) {
			logger.warn("no SPARQL endpoints defined");
		}
		else {
			for (String sparqlEndpoint : sparqlEndpoints) {
				this.sparqlEndpoints.add(new SPARQLRepository(sparqlEndpoint));
			}
		}
	}
	
	public AvgConceptIndegreeResult analyzeConceptsRank(
		Collection<URI> authoritativeConcepts,
		Float randomSubsetSize_percent) throws OpenRDFException
	{
		this.authoritativeConcepts = authoritativeConcepts;
		
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(
			getRankedConcepts(randomSubsetSize_percent),
			progressMonitor,
			"ranking concepts");

		while (conceptIt.hasNext()) {
			rankConcept(conceptIt.next());
		}
		
		return new AvgConceptIndegreeResult(conceptsRank);
	}
	
	private Collection<URI> getRankedConcepts(Float randomSubsetSize_percent) {
		if (randomSubsetSize_percent == null) {
			return authoritativeConcepts;
		}
		else {
			return new RandomSubSet<URI>(authoritativeConcepts, randomSubsetSize_percent);
		}
	}
	
	private void rankConcept(URI concept) 
		throws OpenRDFException 
	{
		for (SPARQLRepository sparqlEndpoint : sparqlEndpoints) {
			rankConceptByEndpoint(concept, sparqlEndpoint);	
		}
	}
	
	private void rankConceptByEndpoint(URI concept, SPARQLRepository endpoint) 
		throws OpenRDFException
	{
		String query = "SELECT distinct ?resource WHERE " +
			"{?resource ?p <"+concept.toString()+">  " +
			"FILTER isIRI(?resource) "+
			"FILTER(regex(str(?resource), \"^http.*\"))}";
		
		TupleQuery endpointQuery = endpoint.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, query);
		
		try {
			TupleQueryResult result = endpointQuery.evaluate();
			addToConceptsRankMap(concept, result);
		}
		catch (QueryEvaluationException e) {
			logger.error("error evaluating concept rank", e);
		}
	}
	
	private void addToConceptsRankMap(URI concept, TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		Set<URI> referencingResourcesOnOtherHost = 
			getReferencingResourcesOnOtherHost(concept, result);

		Set<URI> allReferencingResources = conceptsRank.get(concept);
		if (allReferencingResources == null) {
			allReferencingResources = new HashSet<URI>();
			conceptsRank.put(concept, allReferencingResources);
		}
		allReferencingResources.addAll(referencingResourcesOnOtherHost);
	}
	
	private Set<URI> getReferencingResourcesOnOtherHost(
		URI concept,
		TupleQueryResult result) throws QueryEvaluationException 
	{
		Set<URI> referencingResourcesOnOtherHost = new HashSet<URI>();
		
		while (result.hasNext()) {
			Value referencingResource = result.next().getValue("resource");
			
			try {
				if (referencingResource instanceof URI && 
					isDistinctHost(concept, (URI) referencingResource)) 
				{
					referencingResourcesOnOtherHost.add((URI) referencingResource); 
				}
			}
			catch (URISyntaxException e) {
				// should never happen => don't add to list
			}
		}
		
		return referencingResourcesOnOtherHost;
	}

	private boolean isDistinctHost(URI resource, URI otherResource) 
		throws URISyntaxException 
	{
		String host = new java.net.URI(resource.toString()).getHost();
		String otherHost = new java.net.URI(otherResource.toString()).getHost();
		return !host.equalsIgnoreCase(otherHost);		
	}
}
