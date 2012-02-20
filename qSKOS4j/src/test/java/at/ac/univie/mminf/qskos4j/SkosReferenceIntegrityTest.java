package at.ac.univie.mminf.qskos4j;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class SkosReferenceIntegrityTest extends QSkosTestCase {

	@Test
	public void testAssociativeVsHierarchicalClashes() {
		Collection<Pair<URI>> assHierClashes = qSkosReferenceIntegrityRelations.findAssociativeVsHierarchicalClashes();
		Assert.assertEquals(5, assHierClashes.size());
	}
	
	@Test
	public void testExactVsAssociativeMappingClashes() {
		Collection<Pair<URI>> exAssClashes = qSkosReferenceIntegrityRelations.findExactVsAssociativeMappingClashes();
		Assert.assertEquals(3, exAssClashes.size());
	}
	
}
