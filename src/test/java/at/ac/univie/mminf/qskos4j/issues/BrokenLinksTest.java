package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.outlinks.BrokenLinks;
import at.ac.univie.mminf.qskos4j.issues.outlinks.HttpURIs;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:44
 */
public class BrokenLinksTest {

    private BrokenLinks brokenLinks;

    @Before
    public void setUp() throws RDF4JException, IOException {
        brokenLinks = new BrokenLinks(new HttpURIs());
        brokenLinks.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("resources.rdf").getConnection());
        brokenLinks.setExtAccessDelayMillis(0);
    }

    @Ignore
    @Test
    public void testBrokenLinks() throws RDF4JException {
        Collection<URL> brokenLinkURLs = brokenLinks.getResult().getData();
        Assert.assertEquals(1, brokenLinkURLs.size());
    }

}
