package at.ac.univie.mminf.qskos4j;

import junit.framework.Assert;

import org.junit.Test;

public class DocumentationCoverageTest extends QSkosTestCase {

	@Test
	public void testAverageDocumentationCoverageRatio() {
		qSkosDocumentedConcepts.setPublishingHost("mminf.univie.ac.at");
		float avgDocCoverage = qSkosDocumentedConcepts.getAverageDocumentationCoverageRatio();
		
		Assert.assertEquals(0.214, avgDocCoverage, 0.0005);
	}
	
}
