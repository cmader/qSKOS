package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class InvalidCharactersTest {
    private QSkos qSkosInvalidCharacters;

    @Before
    public void setUp() throws RDF4JException, IOException {
        qSkosInvalidCharacters = new QSkos();
        qSkosInvalidCharacters.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("invalidCharacters.rdf").getConnection());
    }

    @Test
    public void testAllIssues() throws RDF4JException {
        // all issues must run without exception
        try {
            for (Issue issue : qSkosInvalidCharacters.getAllIssues()) {
                issue.getResult();
            }
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
    }
}
