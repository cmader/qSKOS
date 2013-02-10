package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class RelationClashesTest {

    private RelationClashes relationClashes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        relationClashes = new RelationClashes(new HierarchyGraphBuilder(VocabRepository.setUpFromTestResource("associativeVsHierarchicalClashes.rdf")));
    }

    @Test
    public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
        Assert.assertEquals(10, relationClashes.getReport().getData().size());
    }

}
