package at.ac.univie.mminf.qskos4j.servlet;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServlet;

public class ServletInvocationTest {

    private HttpServlet qualityIssuesServlet;

    @Before
    public void setUp() {
        qualityIssuesServlet = new QualityIssueServlet();

    }

    @Test
    public void testInvocation() {

    }
}
