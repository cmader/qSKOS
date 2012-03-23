package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;


public class ResourceAvailabilityTest extends QSkosTestCase {

	private List<URL> availableURLs, notAvailableURLs;
	
	private QSkos qSkosConcepts, qSkosExtResources;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosExtResources = setUpInstance("resources.rdf");
		qSkosExtResources.setUrlDereferencingDelay(0);
	}
	
	@Test
	public void testResourceAvailability() throws OpenRDFException {	
		Map<URL, String> resourceAvailability = qSkosExtResources.findBrokenLinks().getData();
		generateAvailabilityLists(resourceAvailability);
		
		Assert.assertEquals(8, resourceAvailability.keySet().size());
		Assert.assertEquals(7, availableURLs.size());
		Assert.assertEquals(1, notAvailableURLs.size());
	}
	
	private void generateAvailabilityLists(Map<URL, String> resourceAvailability) {
		availableURLs = new ArrayList<URL>();
		notAvailableURLs = new ArrayList<URL>();
		
		for (URL url : resourceAvailability.keySet()) {
			if (resourceAvailability.get(url) == null) {
				notAvailableURLs.add(url);
			}
			else {
				availableURLs.add(url);
			}
		}
	}
	
	@Test
	public void testConceptsNonHttpUriCount() throws OpenRDFException {
		Assert.assertEquals(
			1,
			qSkosConcepts.findNonHttpResources().getData().size());
	}

	@Test
	public void testResourcesNonHttpUriCount() throws OpenRDFException {
		Assert.assertEquals(
			2, 
			qSkosExtResources.findNonHttpResources().getData().size());
	}

}
