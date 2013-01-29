package at.ac.univie.mminf.qskos4j.issues.relations.test;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.relations.SolelyTransitivelyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;

public class SolelyTransitivelyRelatedConceptsTest extends IssueTestCase {

	private SolelyTransitivelyRelatedConcepts solelyTransitivelyRelatedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        solelyTransitivelyRelatedConcepts = (SolelyTransitivelyRelatedConcepts) setUpIssue(
            "solitaryTransitiveRelations.rdf",
            new SolelyTransitivelyRelatedConcepts());
	}

	@Test
	public void testSolitaryTransitiveRelationsCount() throws OpenRDFException {
		Assert.assertEquals(4, solelyTransitivelyRelatedConcepts.getResult().getData().size());
	}
	
}
