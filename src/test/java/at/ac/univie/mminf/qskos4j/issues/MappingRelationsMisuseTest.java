package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.MappingRelationsMisuse;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;

import java.io.IOException;
import java.util.Collection;

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
        Assert.assertEquals(5, mappingRelationsMisuse.getResult().size());
    }

    @Test
    public void affectedConcepts() throws OpenRDFException {
        Collection<Statement> result = mappingRelationsMisuse.getResult();

        Assert.assertTrue(isAffected("conceptA", "conceptB", result));
        Assert.assertTrue(isAffected("conceptC", "conceptD", result));
        Assert.assertTrue(isAffected("conceptI", "conceptJ", result));
        Assert.assertTrue(isAffected("conceptM", "conceptN", result));
        Assert.assertTrue(isAffected("conceptK", "conceptL", result));
    }

    private boolean isAffected(String subjUriSuffix, String objUriSuffix, Collection<Statement> statements) {
        for (Statement statement : statements) {
            if (statement.getSubject().stringValue().endsWith(subjUriSuffix) &&
                statement.getObject().stringValue().endsWith(objUriSuffix))
            {
                return true;
            }
        }
        return false;
    }

}
