package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.UndocumentedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;


public class UndocumentedConceptsTest {

	private UndocumentedConcepts undocumentedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        undocumentedConcepts = new UndocumentedConcepts(new AuthoritativeConcepts(new InvolvedConcepts()));
        undocumentedConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("documentedConcepts.rdf").getConnection());
	}
	
	@Test
	public void testAverageDocumentationCoverageRatio() throws OpenRDFException {
		Assert.assertEquals(1, undocumentedConcepts.getPreparedData().size());
	}
	
}
