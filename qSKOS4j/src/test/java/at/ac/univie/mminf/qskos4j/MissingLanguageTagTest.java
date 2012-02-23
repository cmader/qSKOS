package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryException;

public class MissingLanguageTagTest extends QSkosTestCase {

	private QSkos qSkosComponents, qSkosDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosDeprecatedAndIllegal = setUpInstance("deprecatedAndIllegalTerms.rdf");
	}
	
	@Test
	public void testMissingLangTagCount_1() throws RepositoryException {
		Map<String, Set<Resource>> missingLangTags = qSkosComponents.findMissingLanguageTags();
		Assert.assertEquals(2, missingLangTags.size());
	}
	
	@Test
	public void testMissingLangTagCount_2() throws RepositoryException {
		Map<String, Set<Resource>> missingLangTags = qSkosDeprecatedAndIllegal.findMissingLanguageTags();
		Assert.assertEquals(6, missingLangTags.size());
	}
}
