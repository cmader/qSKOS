package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;


public class UndefinedSkosResourcesTest extends IssueTestCase {
	
	private QSkos qSkosConcepts, qSkosDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpIssue("concepts.rdf");
		qSkosDeprecatedAndIllegal = setUpIssue("deprecatedAndIllegalTerms.rdf");
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
