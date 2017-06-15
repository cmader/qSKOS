package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.labels.InconsistentPrefLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.issues.labels.util.UriSuffixFinder;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;
import java.util.Collection;

public class InconsistentPrefLabelsTest {

    private InconsistentPrefLabels inconsistentPrefLabels;

    @Before
    public void setUp() throws RDF4JException, IOException {
        inconsistentPrefLabels = new InconsistentPrefLabels(new ResourceLabelsCollector());
        inconsistentPrefLabels.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("ambiguousLabels.rdf").getConnection());
    }

    @Test
    public void testUniquePrefLabels() throws RDF4JException {
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
