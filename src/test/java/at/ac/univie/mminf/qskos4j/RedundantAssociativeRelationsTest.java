package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class RedundantAssociativeRelationsTest extends QSkosTestCase {

	private QSkos qSkosRedundantAssociativeRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosRedundantAssociativeRelations = setUpInstance("redundantAssociativeRelations.rdf");
	}
	
	@Test
	public void testRedundantAssociativeRelationsCount() throws OpenRDFException {
		Map<URI, Set<Pair<URI>>> redAssRels = qSkosRedundantAssociativeRelations.
			findRedundantAssociativeRelations().getData();
		
		Assert.assertEquals(4, redAssRels.size());
	}
	
}
