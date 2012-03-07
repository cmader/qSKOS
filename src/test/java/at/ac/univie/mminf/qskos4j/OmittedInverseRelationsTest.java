package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class OmittedInverseRelationsTest extends QSkosTestCase {

	private QSkos qSkosOmittedInverseRelations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosOmittedInverseRelations = setUpInstance("omittedInverseRelations.rdf");
	}
	
	@Test
	public void testMissingInverseRelationsCount() throws OpenRDFException {
		Map<Pair<URI>, String> missingRelations = qSkosOmittedInverseRelations.findOmittedInverseRelations().getData();
		Assert.assertEquals(6, missingRelations.size());
	}
	
}
