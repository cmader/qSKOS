package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;


public class MissingOutLinkTest extends QSkosTestCase {

	private QSkos qSkosComponents, qSkosConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosConcepts = setUpInstance("concepts.rdf");
	}
	
	@Test
	public void testComponentsMissingOutLinkCount() throws OpenRDFException {
		Collection<URI> missingOutLinks = qSkosComponents.findMissingOutLinks().getData();
		
		Assert.assertEquals(
			qSkosComponents.findInvolvedConcepts().getData().size(), 
			missingOutLinks.size());
	}
	
	@Test
	public void testConceptsMissingOutLinkCount() throws OpenRDFException {	
		Collection<URI> extLinks = qSkosConcepts.findMissingOutLinks().getData();
		
		Assert.assertEquals(8, extLinks.size());		
	}

}