package at.ac.univie.mminf.qskos4j;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;

public class ConceptSchemesWithoutTopConceptTest extends QSkosTestCase {

	@Test
	public void testConceptSchemesWithoutTopConceptsCount() {
		List<URI> conceptSchemes = qSkosMissingTopConcepts.findConceptSchemesWithoutTopConcept();
		Assert.assertEquals(1, conceptSchemes.size());
	}
	
}
