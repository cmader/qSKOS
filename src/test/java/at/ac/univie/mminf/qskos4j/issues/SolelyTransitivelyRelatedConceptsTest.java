package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.relations.SolelyTransitivelyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class SolelyTransitivelyRelatedConceptsTest {

	private SolelyTransitivelyRelatedConcepts solelyTransitivelyRelatedConcepts;
	
	@Before
	public void setUp() throws RDF4JException, IOException {
        solelyTransitivelyRelatedConcepts = new SolelyTransitivelyRelatedConcepts();
        solelyTransitivelyRelatedConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("solitaryTransitiveRelations.rdf").getConnection());
	}

	@Test
	public void testSolitaryTransitiveRelationsCount() throws RDF4JException {
		Assert.assertEquals(4, solelyTransitivelyRelatedConcepts.getResult().getData().size());
	}
	
}
