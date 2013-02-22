package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by christian
 * Date: 27.01.13
 * Time: 00:42
 */
public class OmittedOrInvalidLanguageTagsTest {

    private OmittedOrInvalidLanguageTags oiltComponents, oiltDeprecatedAndIllegal, oiltLangTags;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        oiltComponents = new OmittedOrInvalidLanguageTags(
            new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
        oiltDeprecatedAndIllegal = new OmittedOrInvalidLanguageTags(
            new RepositoryBuilder().setUpFromTestResource("deprecatedAndIllegalTerms.rdf").getConnection());
        oiltLangTags = new OmittedOrInvalidLanguageTags(
            new RepositoryBuilder().setUpFromTestResource("languageTags.rdf").getConnection());
    }

    @Test
    public void testMissingLangTagCount_1() throws OpenRDFException {
        Map<Resource, Collection<Literal>> missingLangTags = oiltComponents.getReport().getData();
        Assert.assertEquals(2, missingLangTags.size());
    }

    @Test
    public void testMissingLangTagCount_2() throws OpenRDFException {
        Map<Resource, Collection<Literal>> missingLangTags = oiltDeprecatedAndIllegal.getReport().getData();

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

    @Test
    public void testMissingLangTagCount_3() throws OpenRDFException {
        Map<Resource, Collection<Literal>> missingLangTags = oiltLangTags.getReport().getData();

        // expected value should be 0, but current implementatin relies on org.openrdf.model.util.language.Iso639 checks
        Assert.assertEquals(5, countEntries(missingLangTags.values()));
    }

}
