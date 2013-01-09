package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.general.ExtrapolatedCollectionResult;
import at.ac.univie.mminf.qskos4j.util.RandomSubSet;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;

public class InLinkFinder extends Issue {

	private final Logger logger = LoggerFactory.getLogger(InLinkFinder.class);
	
	private Collection<URI> authoritativeConcepts;
	private Collection<RepositoryConnection> connections;
	private Map<URI, Set<URI>> conceptReferencingResources = new HashMap<URI, Set<URI>>();
    private Integer queryDelayMillis;
	
	public InLinkFinder(VocabRepository vocabRepository, Collection<Repository> repositories)
        throws RepositoryException
	{
		super(vocabRepository);
		
		if (repositories == null || repositories.isEmpty()) {
			logger.warn("no repository for querying defined");
		}
		else {
            connections = new ArrayList<RepositoryConnection>();
            for (Repository repository : repositories) {
                connections.add(repository.getConnection());
            }
		}
	}

    public CollectionResult<URI> findMissingInLinks(
        Collection<URI> authoritativeConcepts,
        Float randomSubsetSize_percent,
        Integer queryDelayMillis) throws OpenRDFException
    {
        this.queryDelayMillis = queryDelayMillis;
        this.authoritativeConcepts = authoritativeConcepts;

        Collection<URI> conceptsToCheck = getConceptsToCheck(randomSubsetSize_percent);

        if (randomSubsetSize_percent != null) {
            logger.info("using subset of " +conceptsToCheck.size()+ " concepts for In-Link checking");
        }

        Iterator<URI> conceptIt = new MonitoredIterator<URI>(
                conceptsToCheck,
                progressMonitor,
                "finding In-Links");

        while (conceptIt.hasNext()) {
            rankConcept(conceptIt.next());
        }

        return new ExtrapolatedCollectionResult<URI>(extractUnreferencedConcepts(), randomSubsetSize_percent);
    }

	private Collection<URI> getConceptsToCheck(Float randomSubsetSize_percent) {
		if (randomSubsetSize_percent == null) {
			return authoritativeConcepts;
		}
		else {
			return new RandomSubSet<URI>(authoritativeConcepts, randomSubsetSize_percent);
		}
	}
	
	private void rankConcept(URI concept)
	{
		for (RepositoryConnection connection : connections) {
            try {
                rankConceptForConnection(concept, connection);
            }
            catch (OpenRDFException e) {
                logger.error("Error ranking concept '" +concept+ "', " + e.toString());
            }
		}
	}
	
	private void rankConceptForConnection(URI concept, RepositoryConnection connection)
          throws OpenRDFException
	{
		String query = "SELECT distinct ?resource WHERE " +
			"{?resource ?p <"+concept.toString()+">  " +
			"FILTER isIRI(?resource) "+
			"FILTER(regex(str(?resource), \"^http.*\"))}";

        // delay to avoid flooding the SPARQL endpoint
        try {
            Thread.sleep(queryDelayMillis);
        }
        catch (InterruptedException e) {
            // ignore this exception
        }

        try {
            TupleQueryResult result = connection.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
            addToConceptsRankMap(concept, result);
        }
        catch (Exception e) {
            logger.error("Error evaluating query '" +query);
        }
	}
	
	private void addToConceptsRankMap(URI concept, TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		Set<URI> referencingResourcesOnOtherHost = 
			getReferencingResourcesOnOtherHost(concept, result);

		Set<URI> allReferencingResources = conceptReferencingResources.get(concept);
		if (allReferencingResources == null) {
			allReferencingResources = new HashSet<URI>();
			conceptReferencingResources.put(concept, allReferencingResources);
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
	
	private Collection<URI> extractUnreferencedConcepts() {
		Collection<URI> unrefConcepts = new HashSet<URI>();
		
		for (URI concept : conceptReferencingResources.keySet()) {
			if (conceptReferencingResources.get(concept).isEmpty()) {
				unrefConcepts.add(concept);
			}
		}
		
		return unrefConcepts;
	}
}
