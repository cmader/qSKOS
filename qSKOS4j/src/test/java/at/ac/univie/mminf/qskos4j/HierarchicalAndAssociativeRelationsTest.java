package at.ac.univie.mminf.qskos4j;

import junit.framework.Assert;

import org.junit.Test;

public class HierarchicalAndAssociativeRelationsTest extends QSkosTestCase {

	@Test
	public void testBothHierarchicallyAndAssociativelyRelatedConceps() {
		Assert.assertEquals(
			4, 
			qSkosHierarchicalAndAssociativeRelations.findAmbiguousRelations().size()
		);
	}
	
}
