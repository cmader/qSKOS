package at.ac.univie.mminf.qskos4j;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class DeprecatedPropertiesTest extends QSkosTestCase {
	
	@Test
	public void testDeprecatedPropertiesCount_1() throws RepositoryException {
		Map<URI, Set<URI>> deprecatedTerms = qSkosConcepts.findDeprecatedProperties();
		Assert.assertEquals(1, deprecatedTerms.size());
	}

	@Test
	public void testDeprecatedPropertiesCount_2() throws RepositoryException {
		Map<URI, Set<URI>> deprecatedTerms = qSkosDeprecatedAndIllegal.findDeprecatedProperties();
		Assert.assertEquals(9, deprecatedTerms.size());
	}
	
}
