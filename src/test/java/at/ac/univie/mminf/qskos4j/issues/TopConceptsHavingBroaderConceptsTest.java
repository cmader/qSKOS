package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.conceptscheme.TopConceptsHavingBroaderConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;


public class TopConceptsHavingBroaderConceptsTest {

	private TopConceptsHavingBroaderConcepts topConceptsHavingBroaderConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        topConceptsHavingBroaderConcepts = new TopConceptsHavingBroaderConcepts();
        topConceptsHavingBroaderConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("topConceptsHavingBroaderConcept.rdf").getConnection());
	}
	
	@Test
	public void testTopConceptsHavingBroaderConceptCount() throws OpenRDFException {
		Assert.assertEquals(4, topConceptsHavingBroaderConcepts.getResult().getData().size());
	}
	
}
