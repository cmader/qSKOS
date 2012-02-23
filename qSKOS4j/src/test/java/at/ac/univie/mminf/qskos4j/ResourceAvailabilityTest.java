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
import org.openrdf.repository.RepositoryException;

public class ResourceAvailabilityTest extends QSkosTestCase {

	private List<URL> availableURLs, notAvailableURLs;
	
	private QSkos qSkosComponents, qSkosConcepts, qSkosExtResources;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosExtResources = setUpInstance("resources.rdf");
	}
	
	@Test
	public void testComponentsResourceAvailability() throws RepositoryException {
		Map<URL, String> resourceAvailability = qSkosComponents.checkResourceAvailability();
		generateAvailabilityLists(resourceAvailability);
		
		Assert.assertEquals(30, resourceAvailability.keySet().size());
		Assert.assertEquals(5, availableURLs.size());
		Assert.assertEquals(25, notAvailableURLs.size());
	}
	
	@Test
	public void testConceptsResourceAvailability() throws RepositoryException {	
		Map<URL, String> resourceAvailability = qSkosConcepts.checkResourceAvailability();
		generateAvailabilityLists(resourceAvailability);
		
		Assert.assertEquals(21, resourceAvailability.keySet().size());		
		Assert.assertEquals(16, availableURLs.size());
		Assert.assertEquals(5, notAvailableURLs.size());
	}
	
	@Test
	public void testResourceAvailability() throws RepositoryException {	
		Map<URL, String> resourceAvailability = qSkosExtResources.checkResourceAvailability();
		generateAvailabilityLists(resourceAvailability);
		
		Assert.assertEquals(7, availableURLs.size());
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
	public void testConceptsNonHttpUriCount() {
		Assert.assertEquals(
			1,
			qSkosConcepts.findNonHttpResources().size());
	}

	@Test
	public void testResourcesNonHttpUriCount() {
		Assert.assertEquals(
			2, 
			qSkosExtResources.findNonHttpResources().size());
	}

}
