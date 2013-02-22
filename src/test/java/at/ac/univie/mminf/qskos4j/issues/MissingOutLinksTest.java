package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.outlinks.MissingOutLinks;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:16
 */
public class MissingOutLinksTest {

    private InvolvedConcepts involvedConceptsForComponents;
    private MissingOutLinks missingOutLinksForComponents, missingOutLinksForConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        involvedConceptsForComponents = new InvolvedConcepts(
            new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
        missingOutLinksForComponents = new MissingOutLinks(new AuthoritativeConcepts(involvedConceptsForComponents));
        missingOutLinksForConcepts = new MissingOutLinks(new AuthoritativeConcepts(new InvolvedConcepts(
            new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection())));
    }

    @Test
    public void testComponentsMissingOutLinkCount() throws OpenRDFException {
        Assert.assertEquals(involvedConceptsForComponents.getReport().getData().size(), missingOutLinksForComponents.getReport().getData().size());
    }

    @Test
    public void testConceptsMissingOutLinkCount() throws OpenRDFException {
        Assert.assertEquals(7, missingOutLinksForConcepts.getReport().getData().size());
    }
}
