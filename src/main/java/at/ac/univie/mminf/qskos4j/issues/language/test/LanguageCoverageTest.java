package at.ac.univie.mminf.qskos4j.issues.language.test;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.language.LanguageCoverage;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import java.awt.image.ComponentSampleModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by christian
 * Date: 27.01.13
 * Time: 00:54
 */
public class LanguageCoverageTest extends IssueTestCase {

    private LanguageCoverage languageCoverage;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        InvolvedConcepts involvedConcepts = (InvolvedConcepts) setUpIssue("components.rdf", new InvolvedConcepts());
        languageCoverage = (LanguageCoverage) setUpIssue("components.rdf", new LanguageCoverage(involvedConcepts));
    }

    @Test
    public void testIncompleteLanguageCoverageCount()
            throws OpenRDFException
    {
        Map<Value, Collection<String>> incompleteLangCoverage = languageCoverage.getResult().getData();
        Assert.assertEquals(13, incompleteLangCoverage.size());
    }

    @Test
    public void testExistResourcesNotHavingEnglishLabels()
            throws OpenRDFException
    {
        Map<Value, Collection<String>> incompleteLangCoverage = languageCoverage.getResult().getData();

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
        Map<Value, Collection<String>> incompleteLangCoverage = languageCoverage.getResult().getData();

        List<Value> foundResources = new ArrayList<Value>();
        for (Value resource : incompleteLangCoverage.keySet()) {
            Collection<String> missingLanguages = incompleteLangCoverage.get(resource);
            if (missingLanguages.size() == 1 && missingLanguages.contains("fr")) {
                foundResources.add(resource);
            }
        }

        Assert.assertEquals(2, foundResources.size());
    }
}
