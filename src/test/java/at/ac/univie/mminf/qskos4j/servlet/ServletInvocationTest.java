package at.ac.univie.mminf.qskos4j.servlet;

import at.ac.univie.mminf.qskos4j.report.UnsupportedReportFormatException;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.xml.sax.SAXException;

import java.io.IOException;

public class ServletInvocationTest extends QskosTestCase {

    private final static String SERVLET_URL = "http://somehost.org/qualityIssueServlet";
    private enum HttpMethod {POST, GET}
    private ServletRunner sr;

    @Before
    public void setUp() throws IOException, OpenRDFException {
        sr = new ServletRunner();
        sr.setContextParameter("repository", setUpRepository("concepts.rdf").getRepository());
        sr.registerServlet("qualityIssueServlet", QualityIssueServlet.class.getName());
    }

    @Test
    public void testHttpGetInvocation() {
        testHttpInvocation(HttpMethod.GET);
    }

    @Test
    public void testHttpPostInvocation() {
        testHttpInvocation(HttpMethod.POST);
    }

    private void testHttpInvocation(HttpMethod method) {
        WebRequest request = null;

        switch (method) {
            case POST:
                request = new PostMethodWebRequest(SERVLET_URL);
                break;

            case GET:
                request = new GetMethodWebRequest(SERVLET_URL);
                break;
        }

        request.setParameter("issueId", "c");
        invokeServlet(request);
    }

    private void invokeServlet(WebRequest request) {
        ServletUnitClient sc = sr.newClient();

        try {
            WebResponse response = sc.getResponse(request);
            Assert.assertNotNull("No response received", response);
            Assert.assertEquals( "content type", "text/plain", response.getContentType() );
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = UnsupportedReportFormatException.class)
    public void unsupportedReportFormatTest() {
        ServletUnitClient sc = sr.newClient();
        WebRequest request = new PostMethodWebRequest(SERVLET_URL);
        request.setParameter("issueId", "c");
        request.setParameter("reportFormat", "bla");

        try {
            sc.getResponse(request);
        }
        catch (IOException e) {
            Assert.fail();
        }
        catch (SAXException e) {
            Assert.fail();
        }
    }

    @Test
    public void unsupportedReportStyleTest() {
        Assert.fail();
    }

}
