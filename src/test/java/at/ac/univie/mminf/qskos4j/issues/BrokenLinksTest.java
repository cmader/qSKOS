package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;


public class BrokenLinksTest extends IssueTestCase {

	private QSkos qSkosConcepts, qSkosExtResources;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpIssue("concepts.rdf");
		qSkosExtResources = setUpIssue("resources.rdf");
		qSkosExtResources.setExtAccessDelayMillis(0);
	}
	
	@Test
	public void testBrokenLinks() throws OpenRDFException {	
		Collection<URL> brokenLinks = qSkosExtResources.findBrokenLinks().getData();
		Assert.assertEquals(1, brokenLinks.size());
	}
	
	@Test
	public void testConceptsNonHttpUriCount() throws OpenRDFException {
		Assert.assertEquals(
			1,
			qSkosConcepts.findNonHttpResources().getData().size());
	}

	@Test
	public void testResourcesNonHttpUriCount() throws OpenRDFException {
		Assert.assertEquals(
			4,
			qSkosExtResources.findNonHttpResources().getData().size());
	}

    @Test
    public void testConceptsHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(
                21,
                (int) qSkosConcepts.findAllHttpUriCount().getData());
    }

    @Test
    public void testResourcesHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(
            8,
            (int) qSkosExtResources.findAllHttpUriCount().getData());
    }

}
