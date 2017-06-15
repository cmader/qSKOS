package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.inlinks.MissingInLinks;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;

import java.io.IOException;
import java.util.Collection;

public class MissingInLinksTest {

    private MissingInLinks missingInLinks;
    private AuthoritativeConcepts authoritativeConcepts;

    @Before
    public void setUp() throws RDF4JException, IOException {
        authoritativeConcepts = new AuthoritativeConcepts(new InvolvedConcepts());

        missingInLinks = new MissingInLinks(authoritativeConcepts);
        missingInLinks.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("rankConcepts.rdf").getConnection());
        missingInLinks.addRepositoryLoopback();
    }

    @Test
    public void testInLinksAsDbPedia() throws RDF4JException {
        authoritativeConcepts.setAuthResourceIdentifier("dbpedia.org");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertTrue(conceptsMissingInLinks.isEmpty());
    }

    @Test
    public void testInLinksAsSTW() throws RDF4JException {
        authoritativeConcepts.setAuthResourceIdentifier("zbw.eu");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertEquals(2, conceptsMissingInLinks.size());
    }

    @Test
    public void testInLinksAsBnf() throws RDF4JException {
        authoritativeConcepts.setAuthResourceIdentifier("data.bnf.fr");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertEquals(1, conceptsMissingInLinks.size());
    }


    @Test
    public void testInLinksAsLocal() throws RDF4JException {
        authoritativeConcepts.setAuthResourceIdentifier("myvocab.org");

        Collection<Resource> conceptsMissingInLinks = missingInLinks.getResult().getData();
        Assert.assertEquals(1, conceptsMissingInLinks.size());
    }
}
