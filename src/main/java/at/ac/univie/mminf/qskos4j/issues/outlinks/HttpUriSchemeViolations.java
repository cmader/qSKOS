package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryResult;

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
public class HttpUriSchemeViolations extends Issue<Collection<String>> {

    public HttpUriSchemeViolations() {
        super("husv",
              "HTTP URI Scheme Violation",
              "Finds triple subjects that are no HTTP URIs",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#http-uri-scheme-violation")
        );
    }

    @Override
    protected Collection<String> computeResult() throws OpenRDFException {
        Set<String> nonHttpURIs = new HashSet<String>();

        RepositoryResult<Statement> allStatements = repCon.getStatements(null, null, null, false);
        while (allStatements.hasNext()) {
            Statement statement = allStatements.next();
            if (isNonHttpURI(statement.getSubject())) {
                nonHttpURIs.add(statement.getSubject().stringValue());
            }
        }

        return nonHttpURIs;
    }

    private boolean isNonHttpURI(Resource resource) {
        if (resource instanceof URI) {
            String uri = resource.stringValue().toLowerCase();
            if (!uri.contains("http://") && !uri.contains("https://")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Report generateReport(Collection<String> preparedData) {
        return new CollectionReport<String>(preparedData);
    }



}
