package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.conceptscheme.MappingRelationsMisuse;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class MappingRelationsMisuseTest {

    private MappingRelationsMisuse mappingRelationsMisuse;

    @Before
    public void setUp() throws IOException, OpenRDFException
    {
        mappingRelationsMisuse = new MappingRelationsMisuse();
        mappingRelationsMisuse.setRepositoryConnection(
            new RepositoryBuilder().setUpFromTestResource("mappingRelationsMisuse.rdf").getConnection());
    }

    @Test
    public void mappingRelationsMisuseCount() throws OpenRDFException {
        Assert.assertEquals(4, mappingRelationsMisuse.getResult().size());
    }

    @Test
    public void affectedConcepts() {
        conceptA, conceptB
        conceptC, conceptD

        2 more...
    }

}
