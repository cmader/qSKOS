package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class RelationClashesTest {

    private RelationClashes relationClashes;

    @Before
    public void setUp() throws RDF4JException, IOException {
        relationClashes = new RelationClashes(new HierarchyGraphBuilder());
        relationClashes.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("relationClashes.rdf").getConnection());
    }

    @Test
    public void testAssociativeVsHierarchicalClashes() throws RDF4JException {
        Assert.assertEquals(10, relationClashes.getResult().getData().size());
    }

}
