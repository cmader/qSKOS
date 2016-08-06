package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 13:48
 */
public class OrphanConceptsTest {

    private OrphanConcepts orphanConceptsForConcepts, orphanConceptsForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        orphanConceptsForConcepts = new OrphanConcepts(new InvolvedConcepts());
        orphanConceptsForConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());

        orphanConceptsForComponents = new OrphanConcepts(new InvolvedConcepts());
        orphanConceptsForComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components_1.rdf").getConnection());
    }

    @Test
    public void testConceptsLooseConceptCount() throws OpenRDFException {
        Collection<Resource> orphanConceptValues = orphanConceptsForConcepts.getResult().getData();
        Assert.assertEquals(7, orphanConceptValues.size());
    }

    @Test
    public void testComponentsLooseConceptCount() throws OpenRDFException {
        Collection<Resource> orphanConceptValues = orphanConceptsForComponents.getResult().getData();
        Assert.assertEquals(2, orphanConceptValues.size());
    }
}
