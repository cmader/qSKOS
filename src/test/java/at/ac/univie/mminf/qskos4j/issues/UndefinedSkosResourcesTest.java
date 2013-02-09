package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.UndefinedSkosResources;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;


public class UndefinedSkosResourcesTest extends QskosTestCase {
	
	private UndefinedSkosResources undefinedSkosResourcesInConcepts, undefinedSkosResourcesInDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        undefinedSkosResourcesInConcepts = new UndefinedSkosResources(setUpRepository("concepts.rdf"));
        undefinedSkosResourcesInDeprecatedAndIllegal = new UndefinedSkosResources(setUpRepository("deprecatedAndIllegalTerms.rdf"));
	}
	
	@Test
	public void testUndefinedSkosResourcesCount_1() throws OpenRDFException {
		Collection<URI> undefRes = undefinedSkosResourcesInConcepts.getReport().getData();
		Assert.assertEquals(3, undefRes.size());
	}

	@Test
	public void testUndefinedSkosResourcesCount_2() throws OpenRDFException {
		Collection<URI> undefRes = undefinedSkosResourcesInDeprecatedAndIllegal.getReport().getData();
		Assert.assertEquals(12, undefRes.size());
	}
	
}
