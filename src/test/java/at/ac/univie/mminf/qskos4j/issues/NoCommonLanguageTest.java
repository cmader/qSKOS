package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.language.NoCommonLanguage;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class NoCommonLanguageTest {

    private NoCommonLanguage noCommonLanguage;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        //noCommonLanguage = new NoCommonLanguage();
        //noCommonLanguage.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("nocommonlanguage.rdf").getConnection());
    }

    @Test
    public void testIncompleteLanguageCoverageCount()
            throws OpenRDFException
    {
        noCommonLanguage.getResult().getData();
        Assert.fail();
    }

}
