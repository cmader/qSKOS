package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 13:48
 */
public class OrphanConceptsTest extends QskosTestCase {

    private OrphanConcepts orphanConceptsForConcepts, orphanConceptsForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        orphanConceptsForConcepts = new OrphanConcepts(new InvolvedConcepts(setUpRepository("concepts.rdf")));
        orphanConceptsForComponents = new OrphanConcepts(new InvolvedConcepts(setUpRepository("components.rdf")));
    }

    @Test
    public void testConceptsLooseConceptCount() throws OpenRDFException {
        Collection<Value> orphanConceptValues = orphanConceptsForConcepts.getResult().getData();
        Assert.assertEquals(7, orphanConceptValues.size());
    }

    @Test
    public void testComponentsLooseConceptCount() throws OpenRDFException {
        Collection<Value> orphanConceptValues = orphanConceptsForComponents.getResult().getData();
        Assert.assertEquals(2, orphanConceptValues.size());
    }
}
