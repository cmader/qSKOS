package at.ac.univie.mminf.qskos4j.issues.inlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.ExtrapolatedCollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.RandomSubSet;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
* Finds concepts that aren't referred by other vocabularies on the Web (
* <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_InLinks">Missing In-Links</a>
* ).
*/
public class MissingInLinks extends Issue<ExtrapolatedCollectionResult<Resource>> {

	private final Logger logger = LoggerFactory.getLogger(MissingInLinks.class);

	private AuthoritativeConcepts authoritativeConcepts;
	private Collection<RepositoryConnection> connections = new ArrayList<>();
	private Map<Resource, Set<IRI>> conceptReferencingResources = new HashMap<>();
    private Integer queryDelayMillis = 0;
    private Float randomSubsetSize_percent;

    public MissingInLinks(AuthoritativeConcepts authoritativeConcepts) {
		super(new IssueDescriptor.Builder("mil",
				"Missing In-Links",
				"Uses the sindice index to find concepts that aren't referenced by other datasets on the Web",
				IssueDescriptor.IssueType.ANALYTICAL)
				.weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#top-concepts-having-broader-concepts")
				.dependentIssue(authoritativeConcepts)
				.build());

        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected ExtrapolatedCollectionResult<Resource> invoke() throws RDF4JException {
        Collection<Resource> conceptsToCheck = getConceptsToCheck(randomSubsetSize_percent);

        if (randomSubsetSize_percent != null) {
            logger.info("Using subset of " +conceptsToCheck.size()+ " concepts for In-Link checking");
        }

        Iterator<Resource> conceptIt = new MonitoredIterator<>(
                conceptsToCheck,
                progressMonitor,
                "finding In-Links");

        while (conceptIt.hasNext()) {
            rankConcept(conceptIt.next());
        }

        return new ExtrapolatedCollectionResult<>(extractUnreferencedConcepts(), randomSubsetSize_percent);
    }

    private Collection<Resource> getConceptsToCheck(Float randomSubsetSize_percent) throws RDF4JException
    {
		if (randomSubsetSize_percent == null) {
			return authoritativeConcepts.getResult().getData();
		}
		else {
			return new RandomSubSet<>(authoritativeConcepts.getResult().getData(), randomSubsetSize_percent);
		}
	}
	
	private void rankConcept(Resource concept)
	{
        if (connections.isEmpty()) {
            logger.warn("no repository for querying defined");
        }

        for (RepositoryConnection connection : connections) {
            rankConceptForConnection(concept, connection);
		}
	}
	
	private void rankConceptForConnection(Resource concept, RepositoryConnection connection)
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
	
	private void addToConceptsRankMap(Resource concept, TupleQueryResult result)
		throws QueryEvaluationException 
	{
		Set<IRI> referencingResourcesOnOtherHost =
			getReferencingResourcesOnOtherHost(concept, result);

		Set<IRI> allReferencingResources = conceptReferencingResources.get(concept);
		if (allReferencingResources == null) {
			allReferencingResources = new HashSet<>();
			conceptReferencingResources.put(concept, allReferencingResources);
		}
		allReferencingResources.addAll(referencingResourcesOnOtherHost);
	}
	
	private Set<IRI> getReferencingResourcesOnOtherHost(
            Value concept,
		TupleQueryResult result) throws QueryEvaluationException 
	{
		Set<IRI> referencingResourcesOnOtherHost = new HashSet<>();
		
		while (result.hasNext()) {
			Value referencingResource = result.next().getValue("resource");
			
			try {
				if (referencingResource instanceof IRI &&
                    concept instanceof IRI &&
					isDistinctHost((IRI) concept, (IRI) referencingResource))
				{
					referencingResourcesOnOtherHost.add((IRI) referencingResource);
				}
			}
			catch (URISyntaxException e) {
				// should never happen => don't add to list
			}
		}
		
		return referencingResourcesOnOtherHost;
	}

	private boolean isDistinctHost(IRI resource, IRI otherResource)
		throws URISyntaxException
	{
		String host = new URI(resource.toString()).getHost();
		String otherHost = new URI(otherResource.toString()).getHost();
		return !host.equalsIgnoreCase(otherHost);		
	}
	
	private Collection<Resource> extractUnreferencedConcepts() {
		Collection<Resource> unrefConcepts = new HashSet<>();
		
		for (Resource concept : conceptReferencingResources.keySet()) {
			if (conceptReferencingResources.get(concept).isEmpty()) {
				unrefConcepts.add(concept);
			}
		}
		
		return unrefConcepts;
	}

    public void setQueryDelayMillis(int delayMillis) {
        queryDelayMillis = delayMillis;
    }

    public void setSubsetSize(Float subsetSizePercent) {
        randomSubsetSize_percent = subsetSizePercent;
    }

    /**
     * Adds the repository containing the vocabulary that's about to test to the list of
     * other repositories. This is only useful for in-link testing purposes.
     */
    public void addRepositoryLoopback() throws RDF4JException {
        connections.add(repCon);
    }

    /**
     * Adds a SPARQL endpoint for estimation of in-links.
     *
     * @param endpointUrl SPARL endpoint URL
     */
    public void addSparqlEndPoint(String endpointUrl) throws RDF4JException {
        Repository repo = new SPARQLRepository(endpointUrl);
        repo.initialize();
        connections.add(repo.getConnection());
    }

}
