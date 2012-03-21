package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;


public class UndocumentedConceptsTest extends QSkosTestCase {

	private QSkos qSkosDocumentedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosDocumentedConcepts = setUpInstance("documentedConcepts.rdf");
	}
	
	@Test
	public void testAverageDocumentationCoverageRatio() throws OpenRDFException {
		Collection<Resource> undocConcepts = qSkosDocumentedConcepts.findUndocumentedConcepts().getData();
		
		Assert.assertEquals(1, undocConcepts.size());
	}
	
}
