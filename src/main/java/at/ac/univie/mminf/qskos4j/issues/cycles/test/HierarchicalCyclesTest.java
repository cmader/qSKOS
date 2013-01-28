package at.ac.univie.mminf.qskos4j.issues.cycles.test;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.cycles.HierarchicalCycles;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
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
public class HierarchicalCyclesTest extends IssueTestCase {

    private HierarchicalCycles hierarchicalCycles, hierarchicalCyclesForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        hierarchicalCycles = (HierarchicalCycles) setUpIssue("cycles.rdf", new HierarchicalCycles());
        hierarchicalCyclesForComponents = (HierarchicalCycles) setUpIssue("components.rdf", new HierarchicalCycles());
    }

    @Test
    public void testCycleCount() throws OpenRDFException {
        Assert.assertEquals(2, hierarchicalCycles.getResult().getData());
    }

    @Test
    public void testComponentsCycleCount() throws OpenRDFException {
        Assert.assertEquals(3, hierarchicalCyclesForComponents.getResult().getData());
    }

}
