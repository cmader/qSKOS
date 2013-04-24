package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.labels.DisjointLabelsViolations;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.issues.labels.util.UriSuffixFinder;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
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
        disjointLabelsViolations = new DisjointLabelsViolations(new ResourceLabelsCollector());
        disjointLabelsViolations.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("ambiguousLabels.rdf").getConnection());
    }

    @Test
    public void testDisjointLabels() throws OpenRDFException {
        Collection<LabelConflict> ambiguousResources = disjointLabelsViolations.getPreparedData().values();

        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptD"));
        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptF"));

        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptE"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptG"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptI"));
    }
}
