package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.MeasureInvoker;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.QSKOSMethodInvocationException;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.UnsupportedMeasureIdException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class InvalidCharactersTest extends IssueTestCase {
    private QSkos qSkosInvalidCharacters;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        qSkosInvalidCharacters = setUpInstance("invalidCharacters.rdf");
        qSkosInvalidCharacters.addRepositoryLoopback();
    }

    @Test
    public void testAllIssues() throws OpenRDFException {
        MeasureInvoker measureInvoker = new MeasureInvoker(qSkosInvalidCharacters);

        // all issues must run without exception
        try {
            measureInvoker.checkForAllMeasures();
        }
        catch (QSKOSMethodInvocationException methInvocExc) {
            Assert.fail(methInvocExc.getMethodName());
        }
        catch (UnsupportedMeasureIdException unsupMesIdExc) {
            Assert.fail(unsupMesIdExc.getUnsupportedId());
        }
    }
}
