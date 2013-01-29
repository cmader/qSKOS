package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.labels.DisjointLabelsViolations;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.issues.labels.util.UriSuffixFinder;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;
import java.util.Collection;

public class DisjointLabelsViolationsTest extends IssueTestCase {

    private DisjointLabelsViolations disjointLabelsViolations;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        disjointLabelsViolations = new DisjointLabelsViolations(new ResourceLabelsCollector(setUpRepository("ambiguousLabels.rdf")));
    }
    @Test
    public void testDisjointLabels() throws OpenRDFException {
        Collection<LabelConflict> ambiguousResources = disjointLabelsViolations.getResult().getData();

        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptD"));
        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptF"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptE"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptG"));
    }
}
