package at.ac.univie.mminf.qskos4j;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class LooseConceptsTest extends QSkosTestCase {

	@Test
	public void testConceptsLooseConceptCount() throws RepositoryException {
		Set<URI> looseConcepts = qSkosConcepts.findLooseConcepts();
		Assert.assertEquals(5, looseConcepts.size());		
	}
	
	@Test
	public void testComponentsLooseConceptCount() throws RepositoryException {
		Set<URI> looseConcepts = qSkosComponents.findLooseConcepts(); 
		Assert.assertEquals(2, looseConcepts.size());		
	}
	
}
