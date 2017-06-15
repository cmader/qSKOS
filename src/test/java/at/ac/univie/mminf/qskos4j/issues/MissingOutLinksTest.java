package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.outlinks.MissingOutLinks;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

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
    public void setUp() throws RDF4JException, IOException {
        involvedConceptsForComponents = new InvolvedConcepts();
        missingOutLinksForComponents = new MissingOutLinks(new AuthoritativeConcepts(involvedConceptsForComponents));
        missingOutLinksForComponents.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());

        missingOutLinksForConcepts = new MissingOutLinks(new AuthoritativeConcepts(new InvolvedConcepts()));
        missingOutLinksForConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("concepts.rdf").getConnection());
    }

    @Test
    public void testComponentsMissingOutLinkCount() throws RDF4JException {
        Assert.assertEquals(involvedConceptsForComponents.getResult().getData().size(),
                            missingOutLinksForComponents.getResult().getData().size());
    }

    @Test
    public void testConceptsMissingOutLinkCount() throws RDF4JException {
        Assert.assertEquals(7, missingOutLinksForConcepts.getResult().getData().size());
    }
}
