package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;

public class SkosReferenceIntegrityTest extends IssueTestCase {

	private QSkos qSkosAssVsHierClashes, qSkosExactVsAssMappingClashes;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosAssVsHierClashes = setUpRepository("associativeVsHierarchicalClashes.rdf");
		qSkosExactVsAssMappingClashes = setUpRepository("exactVsAssociativeMappingClashes.rdf");
	}
	
	@Test
	public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
		Collection<Pair<URI>> assHierClashes = qSkosAssVsHierClashes.findRelationClashes().getData();
		Assert.assertEquals(10, assHierClashes.size());
	}
	
	@Test
	public void testExactVsAssociativeMappingClashes() throws OpenRDFException {
		Collection<Pair<URI>> exAssClashes = qSkosExactVsAssMappingClashes.findMappingClashes().getData();
		Assert.assertEquals(3, exAssClashes.size());
	}
	
}
