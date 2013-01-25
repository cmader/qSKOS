package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;


public class UndocumentedConceptsTest extends IssueTestCase {

	private QSkos qSkosDocumentedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosDocumentedConcepts = setUpIssue("documentedConcepts.rdf");
	}
	
	@Test
	public void testAverageDocumentationCoverageRatio() throws OpenRDFException {
		Collection<Resource> undocConcepts = qSkosDocumentedConcepts.findUndocumentedConcepts().getData();
		
		Assert.assertEquals(1, undocConcepts.size());
	}
	
}
