package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.labels.EmptyLabeledResources;
import at.ac.univie.mminf.qskos4j.issues.labels.util.EmptyLabelsResult;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class EmptyLabeledResourcesTest {

    private EmptyLabeledResources emptyLabeledResources;

    @Before
    public void setUp() throws IOException, RDF4JException {
        emptyLabeledResources = new EmptyLabeledResources();
        emptyLabeledResources.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("emptyLabels.rdf").getConnection());
    }

    @Test
    public void testEmptyLabels() throws RDF4JException {
        EmptyLabelsResult result = emptyLabeledResources.getResult();
        Assert.assertEquals(3, result.occurrenceCount());
    }

}
