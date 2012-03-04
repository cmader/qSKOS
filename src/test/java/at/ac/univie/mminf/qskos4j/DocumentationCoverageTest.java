package at.ac.univie.mminf.qskos4j;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

public class DocumentationCoverageTest extends QSkosTestCase {

	private QSkos qSkosDocumentedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosDocumentedConcepts = setUpInstance("documentedConcepts.rdf");
	}
	
	@Test
	public void testAverageDocumentationCoverageRatio() throws OpenRDFException {
		qSkosDocumentedConcepts.setPublishingHost("mminf.univie.ac.at");
		float avgDocCoverage = qSkosDocumentedConcepts.getAverageDocumentationCoverageRatio();
		
		Assert.assertEquals(0.214, avgDocCoverage, 0.0005);
	}
	
}
