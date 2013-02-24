package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:23
 */
public class HttpURIs extends Issue<Collection<URI>> {

    private Set<URI> httpURIs = new HashSet<URI>();
    private Set<String> invalidResources = new HashSet<String>();

    public HttpURIs(RepositoryConnection repCon) {
        super(repCon,
              "huc",
              "HTTP URI Count",
              "Counts the total number of HTTP URIs",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected Collection<URI> prepareData() throws OpenRDFException {

        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createIRIQuery()).evaluate();

        while (result.hasNext()) {
            Value iri = result.next().getValue("iri");
            addToUrlList(iri);
        }

        return httpURIs;
    }

    @Override
    protected Report prepareReport(Collection<URI> preparedData) {
        return new CollectionReport<URI>(preparedData);
    }

    private String createIRIQuery() {
        return "SELECT DISTINCT ?iri "+
            "FROM default "+
                "WHERE {" +
                    "{{?s ?p ?iri .} UNION "+
                    "{?iri ?p ?o .} UNION "+
                    "{?s ?iri ?p .}} "+
                    "FILTER isIRI(?iri)" +
                "}";
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
}
