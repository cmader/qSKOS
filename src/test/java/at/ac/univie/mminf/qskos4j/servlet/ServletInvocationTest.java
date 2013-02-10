package at.ac.univie.mminf.qskos4j.servlet;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.report.UnsupportedReportFormatException;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
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

public class ServletInvocationTest {

    private final static String SERVLET_URL = "http://somehost.org/qualityIssueServlet";
    private ServletRunner sr;

    @Before
    public void setUp() throws IOException, OpenRDFException {
        sr = new ServletRunner();
        sr.setContextParameter("repository", VocabRepository.setUpFromTestResource("concepts.rdf").getRepository());
        sr.registerServlet("qualityIssueServlet", QualityIssueServlet.class.getName());
    }

    @Test
    public void testHttpGetInvocation() {
        WebRequest request = new GetMethodWebRequest(SERVLET_URL);
        request.setParameter("issueId", new QSkos(null).getAllIssues().iterator().next().getId());
        invokeServlet(request);
    }

    private void invokeServlet(WebRequest request) {
        ServletUnitClient sc = sr.newClient();

        try {
            WebResponse response = sc.getResponse(request);
            Assert.assertNotNull("No response received", response);
            Assert.assertEquals("content type", "text/plain", response.getContentType());
        }
        catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = UnsupportedReportFormatException.class)
    public void unsupportedReportFormatTest() {
        invokeWithInvalidReportParameter("reportFormat");
    }

    @Test(expected = UnsupportedReportStyleException.class)
    public void unsupportedReportStyleTest() {
        invokeWithInvalidReportParameter("reportStyle");
    }

    private void invokeWithInvalidReportParameter(String reportParameter) {
        ServletUnitClient sc = sr.newClient();
        WebRequest request = new GetMethodWebRequest(SERVLET_URL);
        request.setParameter("issueId", new QSkos(null).getAllIssues().iterator().next().getId());
        request.setParameter(reportParameter, "nonsensevalue");

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

}
