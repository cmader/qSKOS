package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.outlinks.HttpUriSchemeViolations;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 17:15
 */
public class NonHttpResourcesTest {

    private HttpUriSchemeViolations httpUriSchemeViolationsForConcepts, httpResourcesForUriSchemeViolations;

    @Before
    public void setUp() throws RDF4JException, IOException {
        httpUriSchemeViolationsForConcepts = new HttpUriSchemeViolations();
        httpUriSchemeViolationsForConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());

        httpResourcesForUriSchemeViolations = new HttpUriSchemeViolations();
        httpResourcesForUriSchemeViolations.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("resources.rdf").getConnection());
    }


    @Test
    public void testConceptsNonHttpUriCount() throws RDF4JException {
        Assert.assertEquals(1, httpUriSchemeViolationsForConcepts.getResult().getData().size());
    }

    @Test
    public void testResourcesNonHttpUriCount() throws RDF4JException {
        Assert.assertEquals(4, httpResourcesForUriSchemeViolations.getResult().getData().size());
    }
}
