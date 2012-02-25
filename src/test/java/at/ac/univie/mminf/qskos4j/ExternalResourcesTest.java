package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class ExternalResourcesTest extends QSkosTestCase {

	private QSkos qSkosComponents, qSkosConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosConcepts = setUpInstance("concepts.rdf");
	}
	
	@Test
	public void testComponentsExternalLinkCount() throws RepositoryException {
		Map<URI, List<URL>> extLinks = qSkosComponents.findExternalResources();
		Assert.assertEquals(0, mapEntriesCount(extLinks));
	}
	
	@Test
	public void testConceptsExternalLinkCount() throws RepositoryException {	
		Map<URI, List<URL>> extLinks = qSkosConcepts.findExternalResources();
		Assert.assertEquals(2, mapEntriesCount(extLinks));		
	}
	
	private int mapEntriesCount(Map<URI, List<URL>> map) {
		int ret = 0;
		
		for (List<URL> entry : map.values()) {
			ret += entry.size();
		}
		
		return ret;
	}

}