package at.ac.univie.mminf.qskos4j.issues.concepts.test;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 12:57
 */
public class AuthoritativeConceptsTest extends IssueTestCase {

    private AuthoritativeConcepts authoritativeConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        authoritativeConcepts = new AuthoritativeConcepts(new InvolvedConcepts(setUpRepository("concepts.rdf")));
        authoritativeConcepts.setAuthResourceIdentifier("zbw.eu");
    }

        @Test
    public void testAuthoritativeConceptsCount() throws OpenRDFException
    {
        Collection<Value> authoritativeConceptValues = authoritativeConcepts.getResult().getData();
        Assert.assertEquals(9, authoritativeConceptValues.size());
    }
}
