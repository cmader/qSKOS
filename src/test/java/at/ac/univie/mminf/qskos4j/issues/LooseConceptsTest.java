package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;


public class LooseConceptsTest extends IssueTestCase {

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
