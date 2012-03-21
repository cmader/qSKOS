package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;


public class OmittedOrInvalidLanguageTagTest extends QSkosTestCase {

	private QSkos qSkosComponents, qSkosDeprecatedAndIllegal;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosDeprecatedAndIllegal = setUpInstance("deprecatedAndIllegalTerms.rdf");
	}
	
	@Test
	public void testMissingLangTagCount_1() throws OpenRDFException {
		Map<Resource, Collection<Literal>> missingLangTags = qSkosComponents.findOmittedOrInvalidLanguageTags().getData();
		
		Assert.assertEquals(2, missingLangTags.size());
	}
	
	@Test
	public void testMissingLangTagCount_2() throws OpenRDFException {
		Map<Resource, Collection<Literal>> missingLangTags = qSkosDeprecatedAndIllegal.findOmittedOrInvalidLanguageTags().getData();
		
		Assert.assertEquals(1, missingLangTags.keySet().size());
		Assert.assertEquals(2, countEntries(missingLangTags.values()));
	}
	
	private int countEntries(Collection<Collection<Literal>> allLiterals) {
		int literalCount = 0;
		for (Collection<Literal> literals : allLiterals) {
			literalCount += literals.size();
		}
		return literalCount;
	}
}
