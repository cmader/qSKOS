package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Finds all "authoritative concepts". See the <a href="https://github.com/cmader/qSKOS/blob/master/README.rdoc">
 * qSKOS readme</a> for further information.
 */
public class AuthoritativeConcepts extends Issue<CollectionResult<Value>> {

    private final Logger logger = LoggerFactory.getLogger(AuthoritativeConcepts.class);

    private String authResourceIdentifier, baseURI;
    private InvolvedConcepts involvedConcepts;

    public AuthoritativeConcepts(InvolvedConcepts involvedConcepts) {
        super("ac",
                "Authoritative Concepts",
                "Finds all authoritative concepts in the vocabulary",
                IssueType.STATISTICAL
        );

        this.involvedConcepts = involvedConcepts;
    }

    public AuthoritativeConcepts(InvolvedConcepts involvedConcepts, String baseURI) {
        this(involvedConcepts);
        this.baseURI = baseURI;
    }

    @Override
    protected CollectionResult<Value> invoke() throws OpenRDFException {
        if (authResourceIdentifier == null) {
            determineAuthResourceIdentifier();
        }

        return new CollectionResult<Value>(extractAuthoritativeConceptsFromInvolved());
    }

    private void determineAuthResourceIdentifier() throws OpenRDFException {
        extractAuthResourceIdentifierFromBaseURI();
        if (authResourceIdentifier == null || authResourceIdentifier.isEmpty()) {
            guessAuthoritativeResourceIdentifier();
        }
    }

    private void extractAuthResourceIdentifierFromBaseURI() {
        if (baseURI != null) {
            try {
                authResourceIdentifier = new java.net.URI(baseURI).getHost();
            }
            catch (URISyntaxException e) {
                // cannot guess authoritative resource identifier
            }
        }
    }

    private void guessAuthoritativeResourceIdentifier() throws OpenRDFException {
        HostNameOccurrencies hostNameOccurencies = new HostNameOccurrencies();

        Iterator<Value> resourcesListIt = new MonitoredIterator<Value>(
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

    private Collection<Value> extractAuthoritativeConceptsFromInvolved() throws OpenRDFException
    {
        Collection<Value> authoritativeConcepts = new HashSet<Value>();

        for (Value concept : involvedConcepts.getResult().getData()) {
            String lowerCaseUriValue = concept.toString().toLowerCase();

            if (lowerCaseUriValue.contains(authResourceIdentifier.toLowerCase()))
            {
                authoritativeConcepts.add(concept);
            }
        }

        return authoritativeConcepts;
    }

    /**
     * Sets a string that is used to identify if an URI is authoritative. This is required to, e.g., find all
     * out-links to distinguish between URIs in the vocabulary namespace and other resources on the Web.
     *
     * @param authResourceIdentifier a string, usually a substring of an URI in the vocabulary's namespace,
     * that uniquely identifies an authoritative URI.
     */
    public void setAuthResourceIdentifier(String authResourceIdentifier) {
        this.authResourceIdentifier = authResourceIdentifier;
    }

    public String getAuthResourceIdentifier() {
        return authResourceIdentifier;
    }

}
