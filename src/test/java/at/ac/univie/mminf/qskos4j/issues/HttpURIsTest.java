package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.outlinks.HttpIRIs;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:33
 */
public class HttpIRIsTest {

    private HttpIRIs httpIRIs1, httpIRIs2;

    @Before
    public void setUp() throws RDF4JException, IOException {
        httpIRIs1 = new HttpIRIs();
        httpIRIs1.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());

        httpIRIs2 = new HttpIRIs();
        httpIRIs2.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("resources.rdf").getConnection());
    }

    @Test
    public void testConceptsHttpUriCount() throws RDF4JException {
        Assert.assertEquals(21, httpIRIs1.getResult().getData().size());
    }

    @Test
    public void testResourcesHttpUriCount() throws RDF4JException {
        Assert.assertEquals(8, httpIRIs2.getResult().getData().size());
    }
}
