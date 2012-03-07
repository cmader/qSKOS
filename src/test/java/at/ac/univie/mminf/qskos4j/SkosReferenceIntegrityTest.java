package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class SkosReferenceIntegrityTest extends QSkosTestCase {

	private QSkos qSkosReferenceIntegrityRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosReferenceIntegrityRelations = setUpInstance("skosReferenceIntegrity.rdf");
	}
	
	@Test
	public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
		Collection<Pair<URI>> assHierClashes = qSkosReferenceIntegrityRelations.findAssociativeVsHierarchicalClashes().getData();
		Assert.assertEquals(5, assHierClashes.size());
	}
	
	@Test
	public void testExactVsAssociativeMappingClashes() throws OpenRDFException {
		Collection<Pair<URI>> exAssClashes = qSkosReferenceIntegrityRelations.findExactVsAssociativeMappingClashes().getData();
		Assert.assertEquals(3, exAssClashes.size());
	}
	
}
