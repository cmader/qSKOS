package at.ac.univie.mminf.qskos4j;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryException;

public class CycleTest extends QSkosTestCase {

	private QSkos qSkosCycles, qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosCycles = setUpInstance("cycles.rdf");
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testComponentsCycleCount() throws RepositoryException {
		Assert.assertEquals(3, qSkosComponents.findHierarchicalCycles().size());
	}
	
	@Test
	public void testCycleCount() throws RepositoryException {
		Assert.assertEquals(2, qSkosCycles.findHierarchicalCycles().size());		
	}
	
}
