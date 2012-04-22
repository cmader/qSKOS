package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.QSkos;


public class ConceptSchemesWithoutTopConceptTest extends IssueTestCase {

	private QSkos qSkosMissingTopConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosMissingTopConcepts = setUpInstance("missingTopConcepts.rdf");
	}
	
	@Test
	public void testConceptSchemesWithoutTopConceptsCount() throws OpenRDFException {
		Collection<URI> conceptSchemes = qSkosMissingTopConcepts.findOmittedTopConcepts().getData();
		Assert.assertEquals(2, conceptSchemes.size());
	}
	
}
