package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.QSkos;


public class UndocumentedConceptsTest extends IssueTestCase {

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
