package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

public class LanguageCoverageTest extends QSkosTestCase {

	private QSkos qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testIncompleteLanguageCoverageCount() 
		throws OpenRDFException 
	{
		Map<Resource, Collection<String>> incompleteLangCoverage = qSkosComponents.getIncompleteLanguageCoverage().getData();
		Assert.assertEquals(13, incompleteLangCoverage.size());		
	}
	
	@Test
	public void testExistResourcesNotHavingEnglishLabels() 
		throws OpenRDFException 
	{
		Map<Resource, Collection<String>> incompleteLangCoverage = qSkosComponents.getIncompleteLanguageCoverage().getData();
		
		boolean englishTagFound = false;
		for (Collection<String> missingLanguages : incompleteLangCoverage.values()) {
			englishTagFound = missingLanguages.contains("en");
			if (englishTagFound) break;
		}
		
		Assert.assertFalse(englishTagFound);		
	}
	
	@Test
	public void testResourcesMissingOnlyFrenchLabelsCount() 
		throws OpenRDFException
	{
		Map<Resource, Collection<String>> incompleteLangCoverage = qSkosComponents.getIncompleteLanguageCoverage().getData();

		List<Resource> foundResources = new ArrayList<Resource>();
		for (Resource resource : incompleteLangCoverage.keySet()) {
			Collection<String> missingLanguages = incompleteLangCoverage.get(resource);
			if (missingLanguages.size() == 1 && missingLanguages.contains("fr")) {
				foundResources.add(resource);
			}
		}
		
		Assert.assertEquals(2, foundResources.size());
	}
}
