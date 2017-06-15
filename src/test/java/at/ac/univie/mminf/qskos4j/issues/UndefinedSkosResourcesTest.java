package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.UndefinedSkosResources;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.URI;

import java.io.IOException;
import java.util.Collection;


public class UndefinedSkosResourcesTest {
	
	private UndefinedSkosResources undefinedSkosResourcesInConcepts, undefinedSkosResourcesInDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws RDF4JException, IOException {
        undefinedSkosResourcesInConcepts = new UndefinedSkosResources();
        undefinedSkosResourcesInConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());
        undefinedSkosResourcesInDeprecatedAndIllegal = new UndefinedSkosResources();
        undefinedSkosResourcesInDeprecatedAndIllegal.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("deprecatedAndIllegalTerms.rdf").getConnection());
	}
	
	@Test
	public void testUndefinedSkosResourcesCount_1() throws RDF4JException {
		Collection<URI> undefRes = undefinedSkosResourcesInConcepts.getResult().getData();
		Assert.assertEquals(3, undefRes.size());
	}

	@Test
	public void testUndefinedSkosResourcesCount_2() throws RDF4JException {
		Collection<URI> undefRes = undefinedSkosResourcesInDeprecatedAndIllegal.getResult().getData();
		Assert.assertEquals(12, undefRes.size());
	}
	
}
