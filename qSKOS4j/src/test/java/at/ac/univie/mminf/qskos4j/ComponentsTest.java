package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class ComponentsTest extends QSkosTestCase {
	
	private QSkos qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testComponentCount() throws RepositoryException {
		long conceptCount = qSkosComponents.getInvolvedConcepts().size();
		
		List<Set<URI>> components = qSkosComponents.findComponents();
		
		long componentCount = components.size();
		Assert.assertEquals(9, componentCount);
		
		Assert.assertTrue(getVertexCount(components) >= conceptCount);
	}
	
	private long getVertexCount(List<Set<URI>> components) {
		long ret = 0;
		
		for (Set<URI> component : components) {
			ret += component.size();
		}
		
		return ret;
	}
	
}
