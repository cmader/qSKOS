package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:43
 */
public class DisconnectedConceptClustersTest {

    private DisconnectedConceptClusters disconnectedConceptClusters;
    private InvolvedConcepts involvedConcepts;

    @Before
    public void setUp() throws RDF4JException, IOException {
        involvedConcepts = new InvolvedConcepts();
        disconnectedConceptClusters = new DisconnectedConceptClusters(involvedConcepts);
        disconnectedConceptClusters.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @Test
    public void testComponentCount() throws RDF4JException {
        long conceptCount = involvedConcepts.getResult().getData().size();
        Collection<Collection<Resource>> components = disconnectedConceptClusters.getResult().getData();

        Assert.assertEquals(7, components.size());
        Assert.assertTrue(getVertexCount(components) <= conceptCount);
    }

    private long getVertexCount(Collection<Collection<Resource>> components) {
        long ret = 0;

        for (Collection<Resource> component : components) {
            ret += component.size();
        }

        return ret;
    }

}
