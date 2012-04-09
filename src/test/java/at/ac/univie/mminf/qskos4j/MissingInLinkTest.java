package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;


public class MissingInLinkTest extends QSkosTestCase {

	private QSkos qSkosRankConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosRankConcepts = setUpInstance("rankConcepts.rdf");
		qSkosRankConcepts.addRepositoryLoopback();
	}
	
	@Test
	public void testInLinksAsDbPedia() throws OpenRDFException {
		qSkosRankConcepts.setAuthoritativeResourceIdentifier("dbpedia.org");
		
		Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
		Assert.assertTrue(conceptsMissingInLinks.isEmpty());		
	}

	@Test
	public void testInLinksAsSTW() throws OpenRDFException {
		qSkosRankConcepts.setAuthoritativeResourceIdentifier("zbw.eu");
		
		Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
		Assert.assertEquals(2, conceptsMissingInLinks.size());		
	}

	@Test
	public void testInLinksAsLocal() throws OpenRDFException {
		qSkosRankConcepts.setAuthoritativeResourceIdentifier("myvocab.org");
		
		Collection<URI> conceptsMissingInLinks = qSkosRankConcepts.findMissingInLinks().getData();
		Assert.assertEquals(1, conceptsMissingInLinks.size());
	}
	
}
