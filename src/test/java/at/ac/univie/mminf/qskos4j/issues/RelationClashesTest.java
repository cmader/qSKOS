package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;

import java.io.IOException;

public class RelationClashesTest {

    private RelationClashes relationClashes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        Repository repository = VocabRepository.setUpFromTestResource("relationClashes.rdf").getRepository();
        relationClashes = new RelationClashes(new HierarchyGraphBuilder(repository));
    }

    @Test
    public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
        Assert.assertEquals(10, relationClashes.getReport().getData().size());
    }

}
