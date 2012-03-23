package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;


public class UndefinedSkosResourcesTest extends QSkosTestCase {
	
	private QSkos qSkosConcepts, qSkosDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosDeprecatedAndIllegal = setUpInstance("deprecatedAndIllegalTerms.rdf");
	}
	
	@Test
	public void testUndefinedSkosResourcesCount_1() throws OpenRDFException {
		Collection<URI> undefRes = qSkosConcepts.findUndefinedSkosResources().getData();
		Assert.assertEquals(3, undefRes.size());
	}

	@Test
	public void testUndefinedSkosResourcesCount_2() throws OpenRDFException {
		Collection<URI> undefRes = qSkosDeprecatedAndIllegal.findUndefinedSkosResources().getData();
		Assert.assertEquals(12, undefRes.size());
	}
	
}
