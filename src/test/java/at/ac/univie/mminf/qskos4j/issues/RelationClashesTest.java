package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;

public class RelationClashesTest {

    private RelationClashes relationClashes;
    private RepositoryConnection repCon;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        repCon = new RepositoryBuilder().setUpFromTestResource("relationClashes.rdf").getConnection();
        relationClashes = new RelationClashes(new HierarchyGraphBuilder(repCon));
    }

    @After
    public void tearDown() throws RepositoryException
    {
        repCon.close();
    }

    @Test
    public void testAssociativeVsHierarchicalClashes() throws OpenRDFException {
        Assert.assertEquals(10, relationClashes.getPreparedData().getData().size());
    }

}
