package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.general.ExtrapolatedCollectionResult;
import at.ac.univie.mminf.qskos4j.util.RandomSubSet;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.url.NoContentTypeProvidedException;
import at.ac.univie.mminf.qskos4j.util.url.UrlDereferencer;
import at.ac.univie.mminf.qskos4j.util.url.UrlNotDereferencableException;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class BrokenLinksFinder extends Issue {
	
	private final Logger logger = LoggerFactory.getLogger(BrokenLinksFinder.class);
	private final String NO_CONTENT_TYPE = "n/a";
	
	private Map<URL, String> urlAvailability = new HashMap<URL, String>();
	private Set<URI> httpURIs;
	private Set<String> invalidResources = new HashSet<String>();
	
	public BrokenLinksFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public ExtrapolatedCollectionResult<URL> findBrokenLinks(
		Float randomSubsetSize_percent,
		Integer urlDereferencingDelayMillis) throws OpenRDFException 
	{
		createAvailabilityMap(randomSubsetSize_percent, urlDereferencingDelayMillis);
		return new ExtrapolatedCollectionResult<URL>(collectUnavailableURLs(), randomSubsetSize_percent);
	}
	
	private void createAvailabilityMap(
		Float randomSubsetSize_percent,
		Integer urlDereferencingDelayMillis) throws OpenRDFException
	{
		findAllHttpURIs();
		dereferenceURIs(randomSubsetSize_percent, urlDereferencingDelayMillis);
	}
	
	public CollectionResult<String> findNonHttpResources() throws OpenRDFException 
	{
		TupleQueryResult result = vocabRepository.query(createNonHttpUriQuery());
		Collection<String> nonHttpUriSet = createNonHttpUriSet(result);
		return new CollectionResult<String>(nonHttpUriSet);
	}
	
	private String createNonHttpUriQuery() {
		return "SELECT DISTINCT ?iri WHERE " +
			"{" +
				"?iri ?p ?obj ." +
				"FILTER isIRI(?iri)"+ 
			"}";
	}
	
	private Collection<String> createNonHttpUriSet(TupleQueryResult result)
		throws OpenRDFException
	{
		Set<String> nonHttpURIs = new HashSet<String>();
		
		while (result.hasNext()) {
			Value iri = result.next().getValue("iri");
			String iriValue = iri.stringValue().toLowerCase();
			if (!iriValue.contains("http://") && !iriValue.contains("https://")) {
				nonHttpURIs.add(iri.stringValue());
			}
		}
		
		return nonHttpURIs;
	}
	
	public Set<URI> findAllHttpURIs()
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
        if (httpURIs != null) {
            return httpURIs;
        }

        httpURIs = new HashSet<URI>();
		TupleQueryResult result = vocabRepository.query(createIRIQuery());
		
		while (result.hasNext()) {
			Value iri = result.next().getValue("iri");
			addToUrlList(iri);
		}
		return httpURIs;
	}
	
	private void addToUrlList(Value iri) {
		try {
			URI uri = new URI(iri.stringValue());

			if (uri.getScheme().startsWith("http")) {
				httpURIs.add(pruneFragment(uri));
			}
		} 
		catch (URISyntaxException e) {
			invalidResources.add(iri.toString());
		}
	}
		
	private URI pruneFragment(URI uri) throws URISyntaxException 
	{
		if (uri.getFragment() != null) {
			int hashIndex = uri.toString().indexOf("#");
			return new URI(uri.toString().substring(0, hashIndex));
		}
		return uri;
	}
	
	private String createIRIQuery() {
		return "SELECT DISTINCT ?iri "+
		"FROM <" +vocabRepository.getVocabContext()+ "> "+
		"WHERE {{{?s ?p ?iri .} UNION "+
			"{?iri ?p ?o .} UNION "+
			"{?s ?iri ?p .}} "+
			"FILTER isIRI(?iri)}";
	}

	private void dereferenceURIs(Float randomSubsetSize_percent, Integer urlDereferencingDelayMillis) 
	{
		Set<URI> urisToBeDereferenced = collectUrisToBeDereferenced(randomSubsetSize_percent);
		Iterator<URI> it = new MonitoredIterator<URI>(urisToBeDereferenced, progressMonitor);
		
		int i = 1;
		while (it.hasNext()) {
			URI uri = it.next();
			
			logger.debug("processing link " +i+ " of "+urisToBeDereferenced.size());
			i++;
			
			// delay to avoid flooding the vocabulary host  
			try {
				Thread.sleep(urlDereferencingDelayMillis);
			} 
			catch (InterruptedException e) {
				// ignore this exception
			}
			
			addToResults(uri);
		}
	}
	
	private Set<URI> collectUrisToBeDereferenced(Float randomSubsetSize_percent) {
		if (randomSubsetSize_percent == null) {
			return httpURIs;
		}

        RandomSubSet<URI> urisToBeDereferenced = new RandomSubSet<URI>(httpURIs, randomSubsetSize_percent);
        logger.info("using subset of " +urisToBeDereferenced.size()+ " URIs for broken link checking");

		return urisToBeDereferenced;
	}
	
	private void addToResults(URI uri) {
		try {
			addToAvailabilityMap(uri.toURL());
		} 
		catch (MalformedURLException e) {
			invalidResources.add(uri.toString());
		}
	}
	
	private void addToAvailabilityMap(URL url) {
		UrlDereferencer dereferencer = new UrlDereferencer();		
		
		String contentType;
		try {
			contentType = dereferencer.getContentType(url);
		}
		catch (UrlNotDereferencableException e) {
			contentType = null;
			logger.debug("url not dereferencable: " +url.toString());
		} 
		catch (NoContentTypeProvidedException e) {
			contentType = NO_CONTENT_TYPE;
			logger.debug("no content type in response header for " +url.toString());
		}

		urlAvailability.put(url, contentType);
	}
	
	private Collection<URL> collectUnavailableURLs() {
		Collection<URL> unavailableURLs = new ArrayList<URL>();
		
		for (URL url : urlAvailability.keySet()) {
			if (urlAvailability.get(url) == null) {
				unavailableURLs.add(url);
			}
		}
		
		return unavailableURLs;
	}
}
