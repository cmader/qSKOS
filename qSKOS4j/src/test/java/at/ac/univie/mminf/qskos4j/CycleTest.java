package at.ac.univie.mminf.qskos4j;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.repository.RepositoryException;

public class CycleTest extends QSkosTestCase {

	@Test
	public void testComponentsCycleCount() throws RepositoryException {
		Assert.assertEquals(3, qSkosComponents.findHierarchicalCycles().size());
	}
	
	@Test
	public void testCycleCount() throws RepositoryException {
		Assert.assertEquals(2, qSkosCycles.findHierarchicalCycles().size());		
	}
	
}
