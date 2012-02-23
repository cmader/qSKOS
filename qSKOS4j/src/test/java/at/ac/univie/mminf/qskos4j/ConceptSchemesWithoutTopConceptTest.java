package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

public class ConceptSchemesWithoutTopConceptTest extends QSkosTestCase {

	private QSkos qSkosMissingTopConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosMissingTopConcepts = setUpInstance("missingTopConcepts.rdf");
	}
	
	@Test
	public void testConceptSchemesWithoutTopConceptsCount() {
		List<URI> conceptSchemes = qSkosMissingTopConcepts.findConceptSchemesWithoutTopConcept();
		Assert.assertEquals(1, conceptSchemes.size());
	}
	
}
