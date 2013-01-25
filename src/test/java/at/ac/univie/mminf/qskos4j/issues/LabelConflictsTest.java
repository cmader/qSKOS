package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.labelissues.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LabelConflictsTest extends IssueTestCase {

	private QSkos qSkosComponents, qSkosRelatedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpIssue("components.rdf");
		qSkosRelatedConcepts = setUpIssue("relatedConcepts.rdf");
		qSkosRelatedConcepts.setAuthoritativeResourceIdentifier("http://aims.fao.org/aos/agrovoc");
	}
	
	@Test
	public void testLabelConflictCount_1() throws OpenRDFException {
		Collection<LabelConflict> allLabelConflicts = qSkosComponents.findOverlappingLabels().getData();
		
		Assert.assertEquals(2, allLabelConflicts.size());
		Assert.assertEquals(4, getDifferentResources(allLabelConflicts).size());
	}

	private Collection<Resource> getDifferentResources(Collection<LabelConflict> labelConflicts)
	{
		Set<Resource> ret = new HashSet<Resource>();
		
		for (LabelConflict labelConflict : labelConflicts) {
			ret.addAll(labelConflict.getAffectedResources());
		}
		
		return ret;
	}
	
	@Test
	public void testLabelConflictCount_2() throws OpenRDFException {
		Assert.assertEquals(0, qSkosRelatedConcepts.findOverlappingLabels().getData().size());
	}
}
