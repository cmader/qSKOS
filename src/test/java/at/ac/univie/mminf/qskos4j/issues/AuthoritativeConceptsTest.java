package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 12:57
 */
public class AuthoritativeConceptsTest {

    private AuthoritativeConcepts authoritativeConcepts;

    @Before
    public void setUp() throws RDF4JException, IOException {
        authoritativeConcepts = new AuthoritativeConcepts(new InvolvedConcepts());
        authoritativeConcepts.setAuthResourceIdentifier("zbw.eu");
        authoritativeConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());
    }

    @Test
    public void testAuthoritativeConceptsCount() throws RDF4JException
    {
        Collection<Resource> authoritativeConceptValues = authoritativeConcepts.getResult().getData();
        Assert.assertEquals(9, authoritativeConceptValues.size());
    }
}
