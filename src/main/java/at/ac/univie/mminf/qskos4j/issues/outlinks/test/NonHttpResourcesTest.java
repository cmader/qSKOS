package at.ac.univie.mminf.qskos4j.issues.outlinks.test;

import at.ac.univie.mminf.qskos4j.issues.outlinks.NonHttpResources;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 17:15
 */
public class NonHttpResourcesTest extends IssueTestCase {

    private NonHttpResources nonHttpResources1, nonHttpResources2;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        nonHttpResources1 = (NonHttpResources) setUpRepository("concepts.rdf", new NonHttpResources());
        nonHttpResources2 = (NonHttpResources) setUpRepository("resources.rdf", new NonHttpResources());
    }


    @Test
    public void testConceptsNonHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(1, nonHttpResources1.getResult().getData().size());
    }

    @Test
    public void testResourcesNonHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(4, nonHttpResources2.getResult().getData().size());
    }
}
