package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 15:43
 */
public class DisconnectedConceptClustersTest {

    private DisconnectedConceptClusters disconnectedConceptClusters;
    private InvolvedConcepts involvedConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        disconnectedConceptClusters = new DisconnectedConceptClusters(new InvolvedConcepts());
        disconnectedConceptClusters.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @Test
    public void testComponentCount() throws OpenRDFException {
        long conceptCount = involvedConcepts.getPreparedData().size();
        Collection<Set<Value>> components = disconnectedConceptClusters.getPreparedData();

        Assert.assertEquals(7, components.size());
        Assert.assertTrue(getVertexCount(components) <= conceptCount);
    }

    private long getVertexCount(Collection<Set<Value>> components) {
        long ret = 0;

        for (Set<Value> component : components) {
            ret += component.size();
        }

        return ret;
    }

}
