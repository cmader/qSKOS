package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;

public class InvolvedConceptsTest {

    private InvolvedConcepts involvedConceptsForConcepts, involvedConceptsForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        involvedConceptsForConcepts = new InvolvedConcepts();
        involvedConceptsForConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());

        involvedConceptsForComponents = new InvolvedConcepts();
        involvedConceptsForComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @Test
    public void testConceptCount_1() throws OpenRDFException
    {
        Collection<Resource> involvedConceptValues = involvedConceptsForConcepts.getResult().getData();
        Assert.assertEquals(10, involvedConceptValues.size());
    }

    @Test
    public void testConceptCount_2() throws OpenRDFException
    {
        Collection<Resource> involvedConceptValues = involvedConceptsForComponents.getResult().getData();
        Assert.assertEquals(21, involvedConceptValues.size());
    }
}
