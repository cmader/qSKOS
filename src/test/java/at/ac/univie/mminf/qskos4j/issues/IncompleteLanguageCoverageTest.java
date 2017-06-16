package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.language.IncompleteLanguageCoverage;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IncompleteLanguageCoverageTest {

    private IncompleteLanguageCoverage incompleteLanguageCoverage;

    @Before
    public void setUp() throws RDF4JException, IOException {
        incompleteLanguageCoverage = new IncompleteLanguageCoverage(new LanguageCoverage(new InvolvedConcepts()));
        incompleteLanguageCoverage.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components_1.rdf").getConnection());
    }

    @Test
    public void testIncompleteLanguageCoverageCount() throws RDF4JException
    {
        Map<Resource, Collection<String>> incompleteLangCoverage = incompleteLanguageCoverage.getResult().getData();
        Assert.assertEquals(13, incompleteLangCoverage.size());
    }

    @Test
    public void testExistResourcesNotHavingEnglishLabels()
            throws RDF4JException
    {
        Map<Resource, Collection<String>> incompleteLangCoverage = incompleteLanguageCoverage.getResult().getData();

        boolean englishTagFound = false;
        for (Collection<String> missingLanguages : incompleteLangCoverage.values()) {
            englishTagFound = missingLanguages.contains("en");
            if (englishTagFound) break;
        }

        Assert.assertFalse(englishTagFound);
    }

    @Test
    public void testResourcesMissingOnlyFrenchLabelsCount()
            throws RDF4JException
    {
        Map<Resource, Collection<String>> incompleteLangCoverage = incompleteLanguageCoverage.getResult().getData();

        List<Value> foundResources = new ArrayList<>();
        for (Value resource : incompleteLangCoverage.keySet()) {
            Collection<String> missingLanguages = incompleteLangCoverage.get(resource);
            if (missingLanguages.size() == 1 && missingLanguages.contains("fr")) {
                foundResources.add(resource);
            }
        }

        Assert.assertEquals(2, foundResources.size());
    }
}
