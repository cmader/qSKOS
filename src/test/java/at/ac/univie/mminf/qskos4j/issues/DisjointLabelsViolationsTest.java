package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.labels.DisjointLabelsViolations;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.issues.labels.util.UriSuffixFinder;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;
import java.util.Collection;

public class DisjointLabelsViolationsTest {

    private DisjointLabelsViolations disjointLabelsViolations;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        disjointLabelsViolations = new DisjointLabelsViolations(new ResourceLabelsCollector(VocabRepository.setUpFromTestResource("ambiguousLabels.rdf")));
    }
    @Test
    public void testDisjointLabels() throws OpenRDFException {
        Collection<LabelConflict> ambiguousResources = disjointLabelsViolations.getReport().getData();

        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptD"));
        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptF"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptE"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptG"));
    }
}
