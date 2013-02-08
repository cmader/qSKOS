package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class InvalidCharactersTest extends QskosTestCase {
    private QSkos qSkosInvalidCharacters;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        qSkosInvalidCharacters = new QSkos(setUpRepository("invalidCharacters.rdf"));
    }

    @Test
    public void testAllIssues() throws OpenRDFException {
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
