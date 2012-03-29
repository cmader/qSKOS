package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;


public class ComponentsTest extends QSkosTestCase {
	
	private QSkos qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testComponentCount() throws OpenRDFException {
		long conceptCount = qSkosComponents.findInvolvedConcepts().getData().size();
		List<Set<Resource>> components = qSkosComponents.findComponents().getConnectedSets();

		Assert.assertEquals(7, components.size());
		Assert.assertTrue(getVertexCount(components) <= conceptCount);
	}
	
	private long getVertexCount(List<Set<Resource>> components) {
		long ret = 0;
		
		for (Set<Resource> component : components) {
			ret += component.size();
		}
		
		return ret;
	}
	
}
