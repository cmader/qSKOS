package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import at.ac.univie.mminf.qskos4j.QSkos;


public class BrokenLinksTest extends IssueTestCase {

	private QSkos qSkosConcepts, qSkosExtResources;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosExtResources = setUpInstance("resources.rdf");
		qSkosExtResources.setUrlDereferencingDelay(0);
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
			2, 
			qSkosExtResources.findNonHttpResources().getData().size());
	}

}
