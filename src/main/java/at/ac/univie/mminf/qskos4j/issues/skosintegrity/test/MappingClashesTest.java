package at.ac.univie.mminf.qskos4j.issues.skosintegrity.test;

import at.ac.univie.mminf.qskos4j.issues.skosintegrity.MappingClashes;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class MappingClashesTest extends IssueTestCase {

    private MappingClashes mappingClashes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        mappingClashes = (MappingClashes) setUpRepository(
                "exactVsAssociativeMappingClashes.rdf",
                new MappingClashes());
    }

    @Test
    public void testExactVsAssociativeMappingClashes() throws OpenRDFException {
        Assert.assertEquals(3, mappingClashes.getResult().getData().size());
    }
}
