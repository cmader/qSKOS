package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.HierarchicalRedundancy;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class HierarchicalRedundancyTest {

    private HierarchicalRedundancy hierarchicalRedundancy;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        hierarchicalRedundancy = new HierarchicalRedundancy(new HierarchyGraphBuilder());
        hierarchicalRedundancy.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("hierarchicalRedundancy.rdf").getConnection());
    }

    @Test
    public void redundancyCount() throws OpenRDFException {
        Assert.assertEquals(5, hierarchicalRedundancy.getResult().getData().size());
    }
}
