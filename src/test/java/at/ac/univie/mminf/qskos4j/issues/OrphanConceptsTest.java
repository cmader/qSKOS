package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 13:48
 */
public class OrphanConceptsTest {

    private OrphanConcepts orphanConceptsForConcepts, orphanConceptsForComponents;
    private RepositoryConnection orphansRepCon, componentsRepCon;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        orphansRepCon = new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection();
        componentsRepCon = new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection();

        orphanConceptsForConcepts = new OrphanConcepts(new InvolvedConcepts(orphansRepCon));
        orphanConceptsForComponents = new OrphanConcepts(new InvolvedConcepts(componentsRepCon));
    }

    @After
    public void tearDown() throws RepositoryException
    {
        orphansRepCon.close();
        componentsRepCon.close();
    }

    @Test
    public void testConceptsLooseConceptCount() throws OpenRDFException {
        Collection<Value> orphanConceptValues = orphanConceptsForConcepts.getReport().getData();
        Assert.assertEquals(7, orphanConceptValues.size());
    }

    @Test
    public void testComponentsLooseConceptCount() throws OpenRDFException {
        Collection<Value> orphanConceptValues = orphanConceptsForComponents.getReport().getData();
        Assert.assertEquals(2, orphanConceptValues.size());
    }
}
