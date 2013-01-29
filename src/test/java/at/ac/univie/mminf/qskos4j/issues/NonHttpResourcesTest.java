package at.ac.univie.mminf.qskos4j.issues;

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

    private NonHttpResources nonHttpResourcesForConcepts, nonHttpResourcesForResources;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        nonHttpResourcesForConcepts = new NonHttpResources(setUpRepository("concepts.rdf"));
        nonHttpResourcesForResources = new NonHttpResources(setUpRepository("resources.rdf"));
    }


    @Test
    public void testConceptsNonHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(1, nonHttpResourcesForConcepts.getResult().getData().size());
    }

    @Test
    public void testResourcesNonHttpUriCount() throws OpenRDFException {
        Assert.assertEquals(4, nonHttpResourcesForResources.getResult().getData().size());
    }
}
