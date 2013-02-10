package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
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
public class OrphanConceptsTest {

    private OrphanConcepts orphanConceptsForConcepts, orphanConceptsForComponents;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        orphanConceptsForConcepts = new OrphanConcepts(new InvolvedConcepts(VocabRepository.setUpFromTestResource("concepts.rdf")));
        orphanConceptsForComponents = new OrphanConcepts(new InvolvedConcepts(VocabRepository.setUpFromTestResource("components.rdf")));
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
