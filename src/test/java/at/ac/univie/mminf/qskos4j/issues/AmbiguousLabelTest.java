package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabelConflict;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;


public class AmbiguousLabelTest extends IssueTestCase {

	private QSkos qSkosAmbiguousLabels;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosAmbiguousLabels = setUpInstance("ambiguousLabels.rdf");
	}
	
	@Test
	public void testUniquePrefLabels() throws OpenRDFException {
		Collection<LabelConflict> ambiguousResources = qSkosAmbiguousLabels.findInconsistentPrefLabels().getData();
		
		Assert.assertTrue(uriSuffixIsPartOfConflict(ambiguousResources, "conceptA"));
		Assert.assertTrue(uriSuffixIsPartOfConflict(ambiguousResources, "conceptA2"));
        Assert.assertTrue(uriSuffixIsPartOfConflict(ambiguousResources, "conceptA3"));
        Assert.assertTrue(uriSuffixIsPartOfConflict(ambiguousResources, "conceptA4"));
		Assert.assertFalse(uriSuffixIsPartOfConflict(ambiguousResources, "conceptB"));
		Assert.assertFalse(uriSuffixIsPartOfConflict(ambiguousResources, "conceptC"));
		Assert.assertFalse(uriSuffixIsPartOfConflict(ambiguousResources, "conceptG"));
	}
	
	@Test 
	public void testDisjointLabels() throws OpenRDFException {
        Collection<LabelConflict> ambiguousResources = qSkosAmbiguousLabels.findDisjointLabelsViolations().getData();
		
		Assert.assertTrue(uriSuffixIsPartOfConflict(ambiguousResources, "conceptD"));
		Assert.assertTrue(uriSuffixIsPartOfConflict(ambiguousResources, "conceptF"));
        Assert.assertFalse(uriSuffixIsPartOfConflict(ambiguousResources, "conceptE"));
        Assert.assertFalse(uriSuffixIsPartOfConflict(ambiguousResources, "conceptG"));
    }
	
	private boolean uriSuffixIsPartOfConflict(
        Collection<LabelConflict> conflicts,
		String suffix)
	{
        if (conflicts != null) {
            for (LabelConflict conflict : conflicts) {
                for (Resource resource : conflict.getAffectedResources()) {
                    if (resource.stringValue().endsWith(suffix)) {
                        return true;
                    }
                }
            }
        }

		return false;
	}
	
}
