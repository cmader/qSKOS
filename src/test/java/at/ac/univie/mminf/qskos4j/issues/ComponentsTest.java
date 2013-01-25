package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;


public class ComponentsTest extends IssueTestCase {
	
	private QSkos qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpIssue("components.rdf");
	}
	
	@Test
	public void testComponentCount() throws OpenRDFException {
		long conceptCount = qSkosComponents.findInvolvedConcepts().getData().size();
		Collection<Set<Resource>> components = qSkosComponents.findClusters().getData();

		Assert.assertEquals(7, components.size());
		Assert.assertTrue(getVertexCount(components) <= conceptCount);
	}
	
	private long getVertexCount(Collection<Set<Resource>> components) {
		long ret = 0;
		
		for (Set<Resource> component : components) {
			ret += component.size();
		}
		
		return ret;
	}
	
}
