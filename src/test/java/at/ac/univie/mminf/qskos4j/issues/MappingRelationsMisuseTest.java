package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.MappingRelationsMisuse;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Statement;

import java.io.IOException;
import java.util.Collection;

public class MappingRelationsMisuseTest {

    private MappingRelationsMisuse mappingRelationsMisuse;

    @Before
    public void setUp() throws IOException, RDF4JException
    {
        mappingRelationsMisuse = new MappingRelationsMisuse(new AuthoritativeConcepts(new InvolvedConcepts()));
        mappingRelationsMisuse.setRepositoryConnection(
            new RepositoryBuilder().setUpFromTestResource("mappingRelationsMisuse.rdf").getConnection());
    }

    @Test
    public void mappingRelationsMisuseCount() throws RDF4JException {
        Assert.assertEquals(5, mappingRelationsMisuse.getResult().getData().size());
    }

    @Test
    public void affectedConcepts() throws RDF4JException {
        Collection<Statement> result = mappingRelationsMisuse.getResult().getData();

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
