package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 27.01.13
 * Time: 00:42
 */
public class OmittedOrInvalidLanguageTagsTest {

    private OmittedOrInvalidLanguageTags oiltComponents, oiltDeprecatedAndIllegal, oiltLangTags;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        oiltComponents = new OmittedOrInvalidLanguageTags();
        oiltComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());

        oiltDeprecatedAndIllegal = new OmittedOrInvalidLanguageTags();
        oiltDeprecatedAndIllegal.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("deprecatedAndIllegalTerms.rdf").getConnection());

        oiltLangTags = new OmittedOrInvalidLanguageTags();
        oiltLangTags.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("languageTags.rdf").getConnection());
    }

    @Test
    public void testMissingLangTagCount_1() throws OpenRDFException {
        Assert.assertEquals(3, oiltComponents.getResult().getData().size());
    }

    @Test
    public void testMissingLangTagCount_2() throws OpenRDFException {
        Assert.assertEquals(2, oiltDeprecatedAndIllegal.getResult().getData().size());
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
        Assert.assertEquals(1, oiltLangTags.getResult().getData().size());
    }

}
