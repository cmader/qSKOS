package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.HierarchicalRedundancy;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class HierarchicalRedundancyTest {

    private HierarchicalRedundancy hierarchicalRedundancy;

    @Before
    public void setUp() throws RDF4JException, IOException {
        hierarchicalRedundancy = new HierarchicalRedundancy(new HierarchyGraphBuilder());
        hierarchicalRedundancy.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("hierarchicalRedundancy.rdf").getConnection());
    }

    @Test
    public void redundancyCount() throws RDF4JException {
        Assert.assertEquals(5, hierarchicalRedundancy.getResult().getData().size());
    }
}
