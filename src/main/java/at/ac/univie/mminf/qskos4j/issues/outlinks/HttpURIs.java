package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:23
 */
public class HttpURIs extends Issue<CollectionResult<URI>> {

    private Set<URI> httpURIs = new HashSet<>();
    private Set<String> invalidResources = new HashSet<>();

    public HttpURIs() {
        super(new IssueDescriptor.Builder("huc",
              "HTTP URI Count",
              "Counts the total number of HTTP URIs",
              IssueDescriptor.IssueType.STATISTICAL).build()
        );
    }

    @Override
    protected CollectionResult<URI> invoke() throws RDF4JException {
        RepositoryResult<Statement> result = repCon.getStatements(null, null, null, false, (Resource) null);
        while (result.hasNext()) {
            Statement st = result.next();

            Collection<Value> tripleValues = new ArrayList<>();
            tripleValues.addAll(Arrays.asList(st.getSubject(), st.getObject(), st.getPredicate()));

            for (Value value : tripleValues) {
                if (value instanceof org.eclipse.rdf4j.model.IRI) addToUrlList(value);
            }
        }

        return new CollectionResult<>(httpURIs);
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
