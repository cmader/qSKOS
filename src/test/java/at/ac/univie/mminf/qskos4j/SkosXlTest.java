package at.ac.univie.mminf.qskos4j;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

public class SkosXlTest extends QSkosTestCase {

	private QSkos qSkosXl;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosXl = setUpInstance("skosxl.rdf");
		qSkosXl.enableSkosXlSupport();
	}
	
	@Test
	public void lexicalRelationsCountTest() throws OpenRDFException {
		Assert.assertEquals(
			(long) 5,
			qSkosXl.findLexicalRelationsCount().getData().longValue()
		);
	}
	
	@Test
	public void omittedLangTagCount() throws OpenRDFException {
		Assert.assertEquals(
			2,
			qSkosXl.findOmittedOrInvalidLanguageTags().getData().keySet().size());
	}
	
	@Test
	public void incompleteLangCovCount() throws OpenRDFException {
		Assert.assertEquals(
			2,
			qSkosXl.findIncompleteLanguageCoverage().getData().keySet().size());
	}

	@Test
	public void labelConflictCount() throws OpenRDFException {
		Assert.assertEquals(
			1,
			qSkosXl.findLabelConflicts().getData().size());
	}

}
