package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Map;

public class OmittedInverseRelationsTest extends IssueTestCase {

	private QSkos qSkosOmittedInverseRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosOmittedInverseRelations = setUpIssue("omittedInverseRelations.rdf");
	}
	
	@Test
	public void testMissingInverseRelationsCount() throws OpenRDFException {
		Map<Pair<Resource>, String> missingRelations = qSkosOmittedInverseRelations.findUnidirectionallyRelatedConcepts().getData();
		Assert.assertEquals(8, missingRelations.size());
	}
	
}
