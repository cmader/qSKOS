package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.cycles.HierarchicalCycles;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 27.01.13
 * Time: 00:20
 */
public class HierarchicalCyclesTest {

    private HierarchicalCycles hierarchicalCycles, hierarchicalCyclesForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        hierarchicalCycles = new HierarchicalCycles(new HierarchyGraphBuilder());
        hierarchicalCycles.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("cycles.rdf").getConnection());

        hierarchicalCyclesForComponents = new HierarchicalCycles(new HierarchyGraphBuilder());
        hierarchicalCyclesForComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @Test
    public void testCycleCount() throws OpenRDFException {
        Assert.assertEquals(2, hierarchicalCycles.getPreparedData().size());
    }

    @Test
    public void testComponentsCycleCount() throws OpenRDFException {
        Assert.assertEquals(3, hierarchicalCyclesForComponents.getPreparedData().size());
    }

}
