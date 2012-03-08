package at.ac.univie.mminf.qskos4j;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;


public class HierarchicalAndAssociativeRelationsTest extends QSkosTestCase {

	private QSkos qSkosHierarchicalAndAssociativeRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosHierarchicalAndAssociativeRelations = setUpInstance("hierarchicalAndAssociativeRelations.rdf");
	}
	
	@Test
	public void testBothHierarchicallyAndAssociativelyRelatedConceps() throws OpenRDFException {
		Assert.assertEquals(
			4, 
			qSkosHierarchicalAndAssociativeRelations.findAmbiguousRelations().getData().size()
		);
	}
	
}
