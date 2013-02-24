package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
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
public class AuthoritativeConcepts extends Issue<Collection<Value>> {

    private final Logger logger = LoggerFactory.getLogger(AuthoritativeConcepts.class);

    private String authResourceIdentifier, baseURI;
    private InvolvedConcepts involvedConcepts;

    public AuthoritativeConcepts(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts.getRepositoryConnection(),
              "ac",
              "Authoritative Concepts",
              "Finds all authoritative concepts in the vocabulary",
              IssueType.STATISTICAL
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected Collection<Value> prepareData() throws OpenRDFException {
        if (authResourceIdentifier == null) {
            determineAuthResourceIdentifier();
        }

        return extractAuthoritativeConceptsFromInvolved();
    }

    @Override
    protected Report prepareReport(Collection<Value> preparedData) {
        return new CollectionReport<Value>(preparedData);
    }

    private void determineAuthResourceIdentifier() throws OpenRDFException {
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

    private void guessAuthoritativeResourceIdentifier() throws OpenRDFException {
        HostNameOccurrencies hostNameOccurencies = new HostNameOccurrencies();

        Iterator<Value> resourcesListIt = new MonitoredIterator<Value>(
                involvedConcepts.getPreparedData(),
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

        for (Value concept : involvedConcepts.getPreparedData()) {
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

    public String getAuthResourceIdentifier() {
        return authResourceIdentifier;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

}
