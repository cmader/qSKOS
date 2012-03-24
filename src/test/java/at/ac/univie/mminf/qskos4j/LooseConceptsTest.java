package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;


public class LooseConceptsTest extends QSkosTestCase {

	private QSkos qSkosConcepts, qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testConceptsLooseConceptCount() throws OpenRDFException {
		Collection<URI> looseConcepts = qSkosConcepts.findOrphanConcepts().getData();
		Assert.assertEquals(7, looseConcepts.size());		
	}
	
	@Test
	public void testComponentsLooseConceptCount() throws OpenRDFException {
		Collection<URI> looseConcepts = qSkosComponents.findOrphanConcepts().getData(); 
		Assert.assertEquals(2, looseConcepts.size());		
	}
	
}
