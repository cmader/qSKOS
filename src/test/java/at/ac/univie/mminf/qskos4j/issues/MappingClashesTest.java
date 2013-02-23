package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.MappingClashes;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class MappingClashesTest {

    private MappingClashes mappingClashes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        mappingClashes = new MappingClashes(
            new RepositoryBuilder().setUpFromTestResource("exactVsAssociativeMappingClashes.rdf").getConnection());
    }

    @Test
    public void testExactVsAssociativeMappingClashes() throws OpenRDFException {
        Assert.assertEquals(3, mappingClashes.getPreparedData().getData().size());
    }
}
