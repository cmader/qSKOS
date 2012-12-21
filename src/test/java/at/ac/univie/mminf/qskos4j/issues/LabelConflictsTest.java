package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabelConflict;

public class LabelConflictsTest extends IssueTestCase {

	private QSkos qSkosComponents, qSkosRelatedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosRelatedConcepts = setUpInstance("relatedConcepts.rdf");
		qSkosRelatedConcepts.setAuthoritativeResourceIdentifier("http://aims.fao.org/aos/agrovoc");
	}
	
	@Test
	public void testLabelConflictCount_1() throws OpenRDFException {
		Collection<LabelConflict> allLabelConflicts = qSkosComponents.findLabelConflicts().getData();
		
		Assert.assertEquals(2, allLabelConflicts.size());
		Assert.assertEquals(4, getDifferentConcepts(allLabelConflicts).size());
	}

	private Collection<URI> getDifferentConcepts(Collection<LabelConflict> labelConflicts)
	{
		Set<URI> ret = new HashSet<URI>();
		
		for (LabelConflict labelConflict : labelConflicts) {
			ret.addAll(labelConflict.getAffectedConcepts());
		}
		
		return ret;
	}
	
	@Test
	public void testLabelConflictCount_2() throws OpenRDFException {
		Assert.assertEquals(0, qSkosRelatedConcepts.findLabelConflicts().getData().size());
	}
}
