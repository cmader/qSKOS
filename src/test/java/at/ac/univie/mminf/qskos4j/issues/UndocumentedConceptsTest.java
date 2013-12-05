package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.UndocumentedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;

import java.io.IOException;


public class UndocumentedConceptsTest {

	private UndocumentedConcepts undocumentedConcepts;
    private RepositoryConnection repCon;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
        repCon = new RepositoryBuilder().setUpFromTestResource("documentedConcepts.rdf").getConnection();
        undocumentedConcepts = new UndocumentedConcepts(new AuthoritativeConcepts(new InvolvedConcepts()));
        undocumentedConcepts.setRepositoryConnection(repCon);
	}
	
	@Test
	public void testAverageDocumentationCoverageRatio() throws OpenRDFException {
		Assert.assertEquals(1, undocumentedConcepts.getResult().getData().size());
	}
	
}
