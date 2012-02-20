package at.ac.univie.mminf.qskos4j;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class IllegalTermsTest extends QSkosTestCase {
	
	@Test
	public void testIllegalTermsCount() throws RepositoryException {
		Map<URI, Set<URI>> deprecatedTerms = qSkosDeprecatedAndIllegal.findIllegalTerms();
		Assert.assertEquals(12, deprecatedTerms.size());
	}
	
}
