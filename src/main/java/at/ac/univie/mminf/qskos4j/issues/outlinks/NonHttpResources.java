package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:30
 *
 * Finds resources not within the HTTP URI scheme (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-HTTP_URI_Scheme_Violation">HTTP URI Scheme Violation</a>.
 */
public class NonHttpResources extends Issue<CollectionResult<String>> {

    public NonHttpResources() {
        super("husv",
              "HTTP URI Scheme Violation",
              "Finds triple subjects that are no HTTP URIs",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionResult<String> invoke() throws OpenRDFException {
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

}
