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

public class SolelyTransitivelyRelatedConceptsTest extends IssueTestCase {

	private QSkos qSkosSolitaryTransitiveRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosSolitaryTransitiveRelations = setUpInstance("solitaryTransitiveRelations.rdf");
	}

	@Test
	public void testSolitaryTransitiveRelationsCount() throws OpenRDFException {
		Collection<Pair<URI>> solitaryTransitiveRelations = 
			qSkosSolitaryTransitiveRelations.findSolelyTransitivelyRelatedConcepts().getData();
		Assert.assertEquals(2, solitaryTransitiveRelations.size());
	}
	
}
