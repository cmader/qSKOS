package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class LanguageCoverageTest extends QSkosTestCase {

	private QSkos qSkosComponents;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
	}
	
	@Test
	public void testIncompleteLanguageCoverageCount() 
		throws RepositoryException 
	{
		Map<URI, Set<String>> incompleteLangCoverage = qSkosComponents.getIncompleteLanguageCoverage();
		Assert.assertEquals(13, incompleteLangCoverage.size());		
	}
	
	@Test
	public void testExistResourcesNotHavingEnglishLabels() 
		throws RepositoryException 
	{
		Map<URI, Set<String>> incompleteLangCoverage = qSkosComponents.getIncompleteLanguageCoverage();
		
		boolean englishTagFound = false;
		for (Set<String> missingLanguages : incompleteLangCoverage.values()) {
			englishTagFound = missingLanguages.contains("en");
			if (englishTagFound) break;
		}
		
		Assert.assertFalse(englishTagFound);		
	}
	
	@Test
	public void testResourcesMissingOnlyFrenchLabelsCount() 
		throws RepositoryException
	{
		Map<URI, Set<String>> incompleteLangCoverage = qSkosComponents.getIncompleteLanguageCoverage();

		List<URI> foundResources = new ArrayList<URI>();
		for (URI resource : incompleteLangCoverage.keySet()) {
			Set<String> missingLanguages = incompleteLangCoverage.get(resource);
			if (missingLanguages.size() == 1 && missingLanguages.contains("fr")) {
				foundResources.add(resource);
			}
		}
		
		Assert.assertEquals(2, foundResources.size());
	}
}
