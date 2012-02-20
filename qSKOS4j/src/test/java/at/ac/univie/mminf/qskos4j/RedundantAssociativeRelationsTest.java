package at.ac.univie.mminf.qskos4j;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class RedundantAssociativeRelationsTest extends QSkosTestCase {

	@Test
	public void testRedundantAssociativeRelationsCount() {
		Map<URI, Set<Pair<URI>>> redAssRels = qSkosRedundantAssociativeRelations.
			findRedundantAssociativeRelations();
		
		Assert.assertEquals(4, redAssRels.size());
	}
	
	@Test
	public void testNotAssociatedSiblingsCount() {
		Map<URI, Set<Pair<URI>>> redAssRels = qSkosRedundantAssociativeRelations.
			findNotAssociatedSiblings();
		
		Assert.assertEquals(2, redAssRels.size());
	}
	
}
