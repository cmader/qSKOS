package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;

public class InvolvedConceptsTest extends QskosTestCase {

    private InvolvedConcepts involvedConceptsForConcepts, involvedConceptsForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        involvedConceptsForConcepts = new InvolvedConcepts(setUpRepository("concepts.rdf"));
        involvedConceptsForComponents = new InvolvedConcepts(setUpRepository("components.rdf"));
    }

    @Test
    public void testConceptCount_1() throws OpenRDFException
    {
        Collection<Value> involvedConceptValues = involvedConceptsForConcepts.getResult().getData();
        Assert.assertEquals(10, involvedConceptValues.size());
    }

    @Test
    public void testConceptCount_2() throws OpenRDFException
    {
        Collection<Value> involvedConceptValues = involvedConceptsForComponents.getResult().getData();
        Assert.assertEquals(21, involvedConceptValues.size());
    }
}
