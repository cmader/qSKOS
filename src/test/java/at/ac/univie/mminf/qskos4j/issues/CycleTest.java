package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;


public class CycleTest extends IssueTestCase {

	private QSkos qSkosCycles, qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosCycles = setUpInstance("cycles.rdf");
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testComponentsCycleCount() throws OpenRDFException {
		Assert.assertEquals(3, qSkosComponents.findHierarchicalCycles().getData().size());
	}
	
	@Test
	public void testCycleCount() throws OpenRDFException {
		Assert.assertEquals(2, qSkosCycles.findHierarchicalCycles().getData().size());		
	}
	
}
