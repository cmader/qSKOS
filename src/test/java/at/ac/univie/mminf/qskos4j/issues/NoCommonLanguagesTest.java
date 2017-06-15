package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.language.NoCommonLanguages;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

public class NoCommonLanguagesTest {

    private NoCommonLanguages noCommonLanguages;

    @Before
    public void setUp() throws RDF4JException, IOException {
        noCommonLanguages = new NoCommonLanguages(new LanguageCoverage(new InvolvedConcepts()));
    }

    @Test
    public void oneCommonLang() throws RDF4JException, IOException {
        noCommonLanguages.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("commonlanguage_en.rdf").getConnection());
        Assert.assertEquals(1, noCommonLanguages.getResult().getData().size());
    }

    @Test
    public void noCommonLang() throws RDF4JException, IOException {
        noCommonLanguages.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("nocommonlanguage.rdf").getConnection());
        Assert.assertEquals(0, noCommonLanguages.getResult().getData().size());
    }

}
