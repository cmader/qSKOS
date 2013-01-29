package at.ac.univie.mminf.qskos4j.issues.skosintegrity.test;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class RelationClashesTest extends IssueTestCase {

    private RelationClashes relationClashes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        VocabRepository repo = setUpRepository("associativeVsHierarchicalClashes.rdf");
        HierarchyGraphBuilder hierarchyGraphBuilder = new HierarchyGraphBuilder(repo);
        relationClashes = new RelationClashes(repo, hierarchyGraphBuilder);
    }

    @Test
    public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
        Assert.assertEquals(10, relationClashes.getResult().getData());
    }

}
