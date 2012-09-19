package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.QSkos;


public class MissingInLinkTest extends IssueTestCase {

	private QSkos qSkosRankConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosRankConcepts = setUpInstance("rankConcepts.rdf");
		qSkosRankConcepts.addRepositoryLoopback();
        qSkosRankConcepts.setExtAccessDelayMillis(0);
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
