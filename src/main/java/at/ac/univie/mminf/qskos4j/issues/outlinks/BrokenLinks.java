package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.ExtrapolatedCollectionResult;
import at.ac.univie.mminf.qskos4j.util.RandomSubSet;
import at.ac.univie.mminf.qskos4j.util.url.NoContentTypeProvidedException;
import at.ac.univie.mminf.qskos4j.util.url.UrlDereferencer;
import at.ac.univie.mminf.qskos4j.util.url.UrlNotDereferencableException;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.impl.IRIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.IRI;
import java.net.URL;
import java.util.*;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:29
 *
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Broken_Links">Broken Links</a>.
 */
public class BrokenLinks extends Issue<ExtrapolatedCollectionResult<URL>> {
	
	private final Logger logger = LoggerFactory.getLogger(BrokenLinks.class);
	private final static String NO_CONTENT_TYPE = "n/a";
	
	private Map<URL, String> urlAvailability = new HashMap<>();
	private Set<String> invalidResources = new HashSet<>();
    private HttpIRIs httpIRIs;
    private Integer extAccessDelayMillis;
    private Float randomSubsetSize_percent;

    public BrokenLinks(HttpIRIs httpIRIs) {
		super(httpIRIs,
              "bl",
              "Broken Links",
              "Checks dereferencability of all links",
              IssueType.ANALYTICAL,
              new IRIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#broken-links")
        );

        this.httpIRIs = httpIRIs;
	}

    @Override
    protected ExtrapolatedCollectionResult<URL> invoke() throws RDF4JException {
        dereferenceIRIs();
		return new ExtrapolatedCollectionResult<>(collectUnavailableURLs(), randomSubsetSize_percent);
	}

    private void dereferenceIRIs() throws RDF4JException
	{
		Collection<IRI> urisToBeDereferenced = collectUrisToBeDereferenced();
		Iterator<IRI> it = new MonitoredIterator<>(urisToBeDereferenced, progressMonitor);
		
		int i = 1;
		while (it.hasNext()) {
			IRI uri = it.next();
			
			logger.debug("processing link " +i+ " of "+urisToBeDereferenced.size());
			i++;
			
			// delay to avoid flooding the vocabulary host  
			try {
				Thread.sleep(extAccessDelayMillis);
			} 
			catch (InterruptedException e) {
				// ignore this exception
			}
			
			addToResults(uri);
		}
	}
	
	private Collection<IRI> collectUrisToBeDereferenced() throws RDF4JException {
		if (randomSubsetSize_percent == null) {
			return httpIRIs.getResult().getData();
		}

        RandomSubSet<IRI> urisToBeDereferenced = new RandomSubSet<>(
            httpIRIs.getResult().getData(),
            randomSubsetSize_percent);
        logger.info("Using subset of " +urisToBeDereferenced.size()+ " IRIs for broken link checking");

		return urisToBeDereferenced;
	}
	
	private void addToResults(IRI uri) {
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
		Collection<URL> unavailableURLs = new ArrayList<>();
		
		for (URL url : urlAvailability.keySet()) {
			if (urlAvailability.get(url) == null) {
				unavailableURLs.add(url);
			}
		}
		
		return unavailableURLs;
	}

    public void setExtAccessDelayMillis(int delayMillis) {
        extAccessDelayMillis = delayMillis;
    }

    public void setSubsetSize(Float subsetSizePercent) {
        randomSubsetSize_percent = subsetSizePercent;
    }

}
