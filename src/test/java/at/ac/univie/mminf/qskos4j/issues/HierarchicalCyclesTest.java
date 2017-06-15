package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.cycles.HierarchicalCycles;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 27.01.13
 * Time: 00:20
 */
public class HierarchicalCyclesTest {

    private HierarchicalCycles hierarchicalCycles, hierarchicalCyclesForComponents;

    @Before
    public void setUp() throws RDF4JException, IOException {
        hierarchicalCycles = new HierarchicalCycles(new HierarchyGraphBuilder());
        hierarchicalCycles.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("cycles.rdf").getConnection());

        hierarchicalCyclesForComponents = new HierarchicalCycles(new HierarchyGraphBuilder());
        hierarchicalCyclesForComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components_1.rdf").getConnection());
    }

    @Test
    public void testCycleCount() throws RDF4JException {
        Assert.assertEquals(3, hierarchicalCycles.getResult().getData().size());
    }

    @Test
    public void testComponentsCycleCount() throws RDF4JException {
        Assert.assertEquals(3, hierarchicalCyclesForComponents.getResult().getData().size());
    }

}
