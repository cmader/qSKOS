package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.Pair;

public class SkosReferenceIntegrityTest extends IssueTestCase {

	private QSkos qSkosAssVsHierClashes, qSkosExactVsAssMappingClashes;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosAssVsHierClashes = setUpInstance("associativeVsHierarchicalClashes.rdf");
		qSkosExactVsAssMappingClashes = setUpInstance("exactVsAssociativeMappingClashes.rdf");
	}
	
	@Test
	public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
		Collection<Pair<URI>> assHierClashes = qSkosAssVsHierClashes.findAssociativeVsHierarchicalClashes().getData();
		Assert.assertEquals(10, assHierClashes.size());
	}
	
	@Test
	public void testExactVsAssociativeMappingClashes() throws OpenRDFException {
		Collection<Pair<URI>> exAssClashes = qSkosExactVsAssMappingClashes.findExactVsAssociativeMappingClashes().getData();
		Assert.assertEquals(3, exAssClashes.size());
	}
	
}
