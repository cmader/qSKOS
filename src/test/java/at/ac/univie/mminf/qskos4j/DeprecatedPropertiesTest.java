package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;


public class DeprecatedPropertiesTest extends QSkosTestCase {
	
	private QSkos qSkosConcepts, qSkosDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosDeprecatedAndIllegal = setUpInstance("deprecatedAndIllegalTerms.rdf");
	}
	
	@Test
	public void testDeprecatedPropertiesCount_1() throws OpenRDFException {
		Map<URI, Collection<URI>> deprecatedTerms = qSkosConcepts.findDeprecatedProperties().getData();
		Assert.assertEquals(1, deprecatedTerms.size());
	}

	@Test
	public void testDeprecatedPropertiesCount_2() throws OpenRDFException {
		Map<URI, Collection<URI>> deprecatedTerms = qSkosDeprecatedAndIllegal.findDeprecatedProperties().getData();
		Assert.assertEquals(9, deprecatedTerms.size());
	}
	
}
