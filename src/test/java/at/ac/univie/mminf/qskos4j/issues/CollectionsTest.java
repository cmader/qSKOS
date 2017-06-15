package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.count.Collections;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:47
 */
public class CollectionsTest {

    private Collections collections;

    @Before
    public void setUp() throws RDF4JException, IOException {
        collections = new Collections();
        collections.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("aggregations.rdf").getConnection());
    }

    @Test
    public void testCollectionsCount() throws RDF4JException
    {
        Assert.assertEquals(4, collections.getResult().getData().longValue());
    }

}
