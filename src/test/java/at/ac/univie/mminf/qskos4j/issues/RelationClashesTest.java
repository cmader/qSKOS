package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class RelationClashesTest extends IssueTestCase {

    private RelationClashes relationClashes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        relationClashes = new RelationClashes(new HierarchyGraphBuilder(setUpRepository("associativeVsHierarchicalClashes.rdf")));
    }

    @Test
    public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
        Assert.assertEquals(10, relationClashes.getResult().getData().size());
    }

}
