package at.ac.univie.mminf.qskos4j;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class SolitaryTransitiveRelationsTest extends QSkosTestCase {

	@Test
	public void testSolitaryTransitiveRelationsCount() {
		Set<Pair<URI>> solitaryTransitiveRelations = 
			qSkosSolitaryTransitiveRelations.findSolitaryTransitiveRelations();
		Assert.assertEquals(2, solitaryTransitiveRelations.size());
	}
	
}
