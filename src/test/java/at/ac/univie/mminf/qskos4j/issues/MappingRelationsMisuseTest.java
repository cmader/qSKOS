package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
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
        mappingRelationsMisuse = new MappingRelationsMisuse(new AuthoritativeConcepts(new InvolvedConcepts()));
        mappingRelationsMisuse.setRepositoryConnection(
            new RepositoryBuilder().setUpFromTestResource("mappingRelationsMisuse.rdf").getConnection());
    }

    @Test
    public void mappingRelationsMisuseCount() throws OpenRDFException {
        Assert.assertEquals(2, mappingRelationsMisuse.getResult().size());

    }

}
