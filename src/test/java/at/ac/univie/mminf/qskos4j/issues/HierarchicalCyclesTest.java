package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.cycles.HierarchicalCycles;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
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
public class HierarchicalCyclesTest extends QskosTestCase {

    private HierarchicalCycles hierarchicalCycles, hierarchicalCyclesForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        VocabRepository cyclesRepo = setUpRepository("cycles.rdf");
        VocabRepository componentsRepo = setUpRepository("components.rdf");

        hierarchicalCycles = new HierarchicalCycles(new HierarchyGraphBuilder(cyclesRepo));
        hierarchicalCyclesForComponents = new HierarchicalCycles(new HierarchyGraphBuilder(componentsRepo));
    }

    @Test
    public void testCycleCount() throws OpenRDFException {
        Assert.assertEquals(2, hierarchicalCycles.getReport().getData().size());
    }

    @Test
    public void testComponentsCycleCount() throws OpenRDFException {
        Assert.assertEquals(3, hierarchicalCyclesForComponents.getReport().getData().size());
    }

}
