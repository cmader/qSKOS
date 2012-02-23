package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

public class TopConceptsHavingBroaderConceptTest extends QSkosTestCase {

	private QSkos qSkosTopConceptsHavingBroaderConcept;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosTopConceptsHavingBroaderConcept = setUpInstance("topConceptsHavingBroaderConcept.rdf");
	}
	
	@Test
	public void testTopConceptsHavingBroaderConceptCount() {
		List<URI> topConceptsHavingBroaderConcept =	qSkosTopConceptsHavingBroaderConcept.
			findTopConceptsHavingBroaderConcept();
		Assert.assertEquals(4, topConceptsHavingBroaderConcept.size());
	}
	
}
