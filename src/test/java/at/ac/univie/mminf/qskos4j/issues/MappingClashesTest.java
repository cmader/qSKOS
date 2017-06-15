package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.MappingClashes;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class MappingClashesTest {

    private MappingClashes mappingClashes;

    @Before
    public void setUp() throws RDF4JException, IOException {
        mappingClashes = new MappingClashes();
        mappingClashes.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("exactVsAssociativeMappingClashes.rdf").getConnection());
    }

    @Test
    public void testExactVsAssociativeMappingClashes() throws RDF4JException {
        Assert.assertEquals(5, mappingClashes.getResult().getData().size());
    }
}
