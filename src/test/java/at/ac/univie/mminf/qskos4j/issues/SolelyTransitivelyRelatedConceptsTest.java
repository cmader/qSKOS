package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;

public class SolelyTransitivelyRelatedConceptsTest extends IssueTestCase {

	private QSkos qSkosSolitaryTransitiveRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosSolitaryTransitiveRelations = setUpIssue("solitaryTransitiveRelations.rdf");
	}

	@Test
	public void testSolitaryTransitiveRelationsCount() throws OpenRDFException {
		Collection<Pair<URI>> solitaryTransitiveRelations = 
			qSkosSolitaryTransitiveRelations.findSolelyTransitivelyRelatedConcepts().getData();
		Assert.assertEquals(4, solitaryTransitiveRelations.size());
	}
	
}
