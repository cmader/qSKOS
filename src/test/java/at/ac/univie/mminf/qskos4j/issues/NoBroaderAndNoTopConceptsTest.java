package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.pp.NoBroaderAndNotTopConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;

public class NoBroaderAndNoTopConceptsTest {

    private NoBroaderAndNotTopConcepts noBroaderAndNotTopConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        noBroaderAndNotTopConcepts = new NoBroaderAndNotTopConcepts(VocabRepository.setUpFromTestResource("noBroaderAndNotTopConcepts.rdf"));
    }

    @Test
    public void testConceptCount() throws OpenRDFException
    {
        Collection<Value> concepts = noBroaderAndNotTopConcepts.getReport().getData();
        Assert.assertEquals(2, concepts.size());
    }

}
