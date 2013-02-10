package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.conceptscheme.TopConceptsHavingBroaderConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;


public class TopConceptsHavingBroaderConceptsTest {

	private TopConceptsHavingBroaderConcepts topConceptsHavingBroaderConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        topConceptsHavingBroaderConcepts = new TopConceptsHavingBroaderConcepts(VocabRepository.setUpFromTestResource("topConceptsHavingBroaderConcept.rdf"));
	}
	
	@Test
	public void testTopConceptsHavingBroaderConceptCount() throws OpenRDFException {
		Assert.assertEquals(4, topConceptsHavingBroaderConcepts.getReport().getData().size());
	}
	
}
