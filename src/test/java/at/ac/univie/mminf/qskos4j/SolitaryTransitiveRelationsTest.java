package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class SolitaryTransitiveRelationsTest extends QSkosTestCase {

	private QSkos qSkosSolitaryTransitiveRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosSolitaryTransitiveRelations = setUpInstance("solitaryTransitiveRelations.rdf");
	}

	@Test
	public void testSolitaryTransitiveRelationsCount() throws OpenRDFException {
		Set<Pair<URI>> solitaryTransitiveRelations = 
			qSkosSolitaryTransitiveRelations.findSolitaryTransitiveRelations();
		Assert.assertEquals(2, solitaryTransitiveRelations.size());
	}
	
}
