package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

public class IllegalTermsTest extends QSkosTestCase {

	private QSkos qSkosDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosDeprecatedAndIllegal = setUpInstance("deprecatedAndIllegalTerms.rdf");
	}
		
	@Test
	public void testIllegalTermsCount() throws OpenRDFException {
		Map<URI, Collection<URI>> deprecatedTerms = qSkosDeprecatedAndIllegal.findIllegalTerms().getData();
		Assert.assertEquals(12, deprecatedTerms.size());
	}
	
}
