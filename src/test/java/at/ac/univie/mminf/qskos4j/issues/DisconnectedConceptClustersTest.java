package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;

import java.io.IOException;
import java.util.Collection;

public class DisconnectedConceptClustersTest {

    private InvolvedConcepts involvedConcepts;
    private DisconnectedConceptClusters disconnectedConceptClusters;

    @Test
    public void testComponentCount() throws RDF4JException, IOException {
        setUp("components_1.rdf");

        long conceptCount = involvedConcepts.getResult().getData().size();
        Collection<Collection<Resource>> components = disconnectedConceptClusters.getResult().getData();

        Assert.assertEquals(7, components.size());
        Assert.assertTrue(getVertexCount(components) <= conceptCount);
    }

    private void setUp(String filename) throws IOException, RDF4JException {
        involvedConcepts = new InvolvedConcepts();
        disconnectedConceptClusters = new DisconnectedConceptClusters(involvedConcepts);
        disconnectedConceptClusters.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource(filename).getConnection());
    }

    private long getVertexCount(Collection<Collection<Resource>> components) {
        long ret = 0;

        for (Collection<Resource> component : components) {
            ret += component.size();
        }

        return ret;
    }

    @Test
    public void testComponents_ok() throws RDF4JException, IOException {
        setUp("components_2.rdf");
        Assert.assertFalse(disconnectedConceptClusters.getResult().isProblematic());
    }

}
