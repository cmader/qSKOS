package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.relations.SolelyTransitivelyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class SolelyTransitivelyRelatedConceptsTest {

	private SolelyTransitivelyRelatedConcepts solelyTransitivelyRelatedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        solelyTransitivelyRelatedConcepts = new SolelyTransitivelyRelatedConcepts(VocabRepository.setUpFromTestResource("solitaryTransitiveRelations.rdf"));
	}

	@Test
	public void testSolitaryTransitiveRelationsCount() throws OpenRDFException {
		Assert.assertEquals(4, solelyTransitivelyRelatedConcepts.getReport().getData().size());
	}
	
}
