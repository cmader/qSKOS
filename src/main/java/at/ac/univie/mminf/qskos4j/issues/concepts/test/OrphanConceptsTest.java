package at.ac.univie.mminf.qskos4j.issues.concepts.test;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
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
public class OrphanConceptsTest extends IssueTestCase {

    private OrphanConcepts orphanConcepts1, orphanConcepts2;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        orphanConcepts1 = (OrphanConcepts) setUpIssue("concepts.rdf", new OrphanConcepts(new InvolvedConcepts()));
        orphanConcepts2 = (OrphanConcepts) setUpIssue("components.rdf", new OrphanConcepts(new InvolvedConcepts()));
    }

    @Test
    public void testConceptsLooseConceptCount() throws OpenRDFException {
        Collection<Value> orphanConceptValues = orphanConcepts1.getResult().getData();
        Assert.assertEquals(7, orphanConceptValues.size());
    }

    @Test
    public void testComponentsLooseConceptCount() throws OpenRDFException {
        Collection<Value> orphanConceptValues = orphanConcepts2.getResult().getData();
        Assert.assertEquals(2, orphanConceptValues.size());
    }
}
