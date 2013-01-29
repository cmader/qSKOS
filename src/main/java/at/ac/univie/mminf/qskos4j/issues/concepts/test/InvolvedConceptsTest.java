package at.ac.univie.mminf.qskos4j.issues.concepts.test;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;

public class InvolvedConceptsTest extends IssueTestCase {

    private InvolvedConcepts involvedConcepts1, involvedConcepts2;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        involvedConcepts1 = (InvolvedConcepts) setUpRepository("concepts.rdf", new InvolvedConcepts());
        involvedConcepts2 = (InvolvedConcepts) setUpRepository("components.rdf", new InvolvedConcepts());
    }

    @Test
    public void testConceptCount_1() throws OpenRDFException
    {
        Collection<Value> involvedConceptValues = involvedConcepts1.getResult().getData();
        Assert.assertEquals(10, involvedConceptValues.size());
    }

    @Test
    public void testConceptCount_2() throws OpenRDFException
    {
        Collection<Value> involvedConceptValues = involvedConcepts2.getResult().getData();
        Assert.assertEquals(21, involvedConceptValues.size());
    }
}
