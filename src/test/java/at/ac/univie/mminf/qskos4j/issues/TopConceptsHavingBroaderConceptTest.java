package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;


public class TopConceptsHavingBroaderConceptTest extends IssueTestCase {

	private QSkos qSkosTopConceptsHavingBroaderConcept;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosTopConceptsHavingBroaderConcept = setUpIssue("topConceptsHavingBroaderConcept.rdf");
	}
	
	@Test
	public void testTopConceptsHavingBroaderConceptCount() throws OpenRDFException {
		Collection<URI> topConceptsHavingBroaderConcept =	qSkosTopConceptsHavingBroaderConcept.
			findTopConceptsHavingBroaderConcepts().getData();
		Assert.assertEquals(4, topConceptsHavingBroaderConcept.size());
	}
	
}
