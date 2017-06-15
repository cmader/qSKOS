package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Finds all "authoritative concepts". See the <a href="https://github.com/cmader/qSKOS/blob/master/README.rdoc">
 * qSKOS readme</a> for further information.
 */
public class AuthoritativeConcepts extends Issue<CollectionResult<Resource>> {

    private final Logger logger = LoggerFactory.getLogger(AuthoritativeConcepts.class);

    private String authResourceIdentifier, baseURI;
    private InvolvedConcepts involvedConcepts;

    public AuthoritativeConcepts(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts,
            "ac",
            "Authoritative Concepts",
            "Finds all authoritative concepts in the vocabulary",
            IssueType.STATISTICAL
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
        getAuthResourceIdentifier();
        return new CollectionResult<Resource>(extractAuthoritativeConceptsFromInvolved());
    }

    private void determineAuthResourceIdentifier() throws RDF4JException {
        try {
            extractAuthResourceIdentifierFromBaseURI();
        }
        catch (Exception e) {
            guessAuthoritativeResourceIdentifier();
        }
    }

    private void extractAuthResourceIdentifierFromBaseURI() throws Exception{
        authResourceIdentifier = new java.net.URI(baseURI).getHost();
    }

    private void guessAuthoritativeResourceIdentifier() throws RDF4JException {
        HostNameOccurrencies hostNameOccurencies = new HostNameOccurrencies();

        Iterator<Resource> resourcesListIt = new MonitoredIterator<Resource>(
                involvedConcepts.getResult().getData(),
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
        logger.info("Guessed authoritative resource identifier: '" +authResourceIdentifier+ "'");
    }

    private Collection<Resource> extractAuthoritativeConceptsFromInvolved() throws RDF4JException
    {
        Collection<Resource> authoritativeConcepts = new HashSet<Resource>();

        for (Resource concept : involvedConcepts.getResult().getData()) {
            String lowerCaseUriValue = concept.toString().toLowerCase();

            if (lowerCaseUriValue.contains(authResourceIdentifier.toLowerCase()))
            {
                authoritativeConcepts.add(concept);
            }
        }

        return authoritativeConcepts;
    }

    public void setAuthResourceIdentifier(String authResourceIdentifier) {
        this.authResourceIdentifier = authResourceIdentifier;
        reset();
    }

    public String getAuthResourceIdentifier() throws RDF4JException {
        if (authResourceIdentifier == null) {
            determineAuthResourceIdentifier();
        }
        return authResourceIdentifier;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

}
