package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
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
 * Time: 13:48
 */
public class OrphanConceptsTest {

    private OrphanConcepts orphanConceptsForConcepts, orphanConceptsForComponents;

    @Before
    public void setUp() throws RDF4JException, IOException {
        orphanConceptsForConcepts = new OrphanConcepts(new InvolvedConcepts());
        orphanConceptsForConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());

        orphanConceptsForComponents = new OrphanConcepts(new InvolvedConcepts());
        orphanConceptsForComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @Test
    public void testConceptsLooseConceptCount() throws RDF4JException {
        Collection<Resource> orphanConceptValues = orphanConceptsForConcepts.getResult().getData();
        Assert.assertEquals(7, orphanConceptValues.size());
    }

    @Test
    public void testComponentsLooseConceptCount() throws RDF4JException {
        Collection<Resource> orphanConceptValues = orphanConceptsForComponents.getResult().getData();
        Assert.assertEquals(2, orphanConceptValues.size());
    }
}
