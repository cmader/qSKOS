package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.labels.EmptyLabeledResources;
import at.ac.univie.mminf.qskos4j.issues.labels.util.EmptyLabelsResult;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class EmptyLabeledResourcesTest {

    private EmptyLabeledResources emptyLabeledResources;

    @Before
    public void setUp() throws IOException, OpenRDFException {
        emptyLabeledResources = new EmptyLabeledResources();
        emptyLabeledResources.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("emptyLabels.rdf").getConnection());
    }

    @Test
    public void testEmptyLabels() throws OpenRDFException {
        EmptyLabelsResult result = emptyLabeledResources.getResult();
        Assert.assertEquals(3, result.occurrenceCount());
    }

}
