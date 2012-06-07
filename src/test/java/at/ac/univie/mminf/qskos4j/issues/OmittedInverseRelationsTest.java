package at.ac.univie.mminf.qskos4j.issues;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.Pair;

public class OmittedInverseRelationsTest extends IssueTestCase {

	private QSkos qSkosOmittedInverseRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosOmittedInverseRelations = setUpInstance("omittedInverseRelations.rdf");
	}
	
	@Test
	public void testMissingInverseRelationsCount() throws OpenRDFException {
		Map<Pair<Resource>, String> missingRelations = qSkosOmittedInverseRelations.findOmittedInverseRelations().getData();
		Assert.assertEquals(6, missingRelations.size());
	}
	
}
