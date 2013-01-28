package at.ac.univie.mminf.qskos4j.issues.labels.test;

import at.ac.univie.mminf.qskos4j.issues.labels.InconsistentPrefLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.UriSuffixFinder;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;
import java.util.Collection;

public class InconsistentPrefLabelsTest extends IssueTestCase {

    private InconsistentPrefLabels inconsistentPrefLabels;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        inconsistentPrefLabels = (InconsistentPrefLabels) setUpIssue("ambiguousLabels.rdf", new InconsistentPrefLabels());
    }

    @Test
    public void testUniquePrefLabels() throws OpenRDFException {
        Collection<LabelConflict> ambiguousResources = inconsistentPrefLabels.getResult().getData();

        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptA"));
        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptA2"));
        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptA3"));
        Assert.assertTrue(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptA4"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptB"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptC"));
        Assert.assertFalse(UriSuffixFinder.isPartOfConflict(ambiguousResources, "conceptG"));
    }

}
