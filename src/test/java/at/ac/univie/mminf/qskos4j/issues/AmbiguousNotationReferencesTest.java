package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.AmbiguousNotationReferences;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian on 25.09.14.
 */
public class AmbiguousNotationReferencesTest {

    private AmbiguousNotationReferences ambiguousNotationReferences;

    @Before
    public void setUp() throws IOException, OpenRDFException
    {
        ambiguousNotationReferences = new AmbiguousNotationReferences(new AuthoritativeConcepts(new InvolvedConcepts()));
        ambiguousNotationReferences.setRepositoryConnection(
                new RepositoryBuilder().setUpFromTestResource("ambiguousNotationReferences.rdf").getConnection());
    }

    @Test
    public void mappingRelationsMisuseCount() throws OpenRDFException {
        //Assert.assertEquals(5, mappingRelationsMisuse.getResult().getData().size());
        Assert.fail();
    }
}
