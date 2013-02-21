package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;

import java.io.IOException;

/**
 * Created by christian
 * Date: 27.01.13
 * Time: 00:20
 */
public class HierarchicalCyclesFullTest {

    private HierarchicalCycles hierarchicalCycles, hierarchicalCyclesForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        Repository cyclesRepo = VocabRepository.setUpFromTestResource("cycles.rdf").getRepository();
        Repository componentsRepo = VocabRepository.setUpFromTestResource("components.rdf").getRepository();

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
