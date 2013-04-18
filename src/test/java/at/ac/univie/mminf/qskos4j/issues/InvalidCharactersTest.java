package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class InvalidCharactersTest {
    private QSkos qSkosInvalidCharacters;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        qSkosInvalidCharacters = new QSkos();
        qSkosInvalidCharacters.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("invalidCharacters.rdf").getConnection());
    }

    @Test
    public void testAllIssues() throws OpenRDFException {
        // all issues must run without exception
        try {
            for (Issue issue : qSkosInvalidCharacters.getAllIssues()) {
                issue.getPreparedData();
            }
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
    }
}
