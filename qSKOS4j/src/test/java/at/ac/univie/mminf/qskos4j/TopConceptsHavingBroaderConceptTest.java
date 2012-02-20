package at.ac.univie.mminf.qskos4j;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;

public class TopConceptsHavingBroaderConceptTest extends QSkosTestCase {

	@Test
	public void testTopConceptsHavingBroaderConceptCount() {
		List<URI> topConceptsHavingBroaderConcept =	qSkosTopConceptsHavingBroaderConcept.
			findTopConceptsHavingBroaderConcept();
		Assert.assertEquals(4, topConceptsHavingBroaderConcept.size());
	}
	
}
