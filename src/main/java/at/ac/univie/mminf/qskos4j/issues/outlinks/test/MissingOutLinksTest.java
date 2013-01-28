package at.ac.univie.mminf.qskos4j.issues.outlinks.test;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.outlinks.MissingOutLinks;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 16:16
 */
public class MissingOutLinksTest extends IssueTestCase {

    private InvolvedConcepts involvedConcepts;
    private MissingOutLinks missingOutLinks1, missingOutLinks2;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        involvedConcepts = (InvolvedConcepts) setUpIssue("components.rdf", new InvolvedConcepts());
        missingOutLinks1 = (MissingOutLinks) setUpIssue(
            "components.rdf",
            new MissingOutLinks(new AuthoritativeConcepts(involvedConcepts)));

        missingOutLinks2 = (MissingOutLinks) setUpIssue(
            "concepts.rdf",
            new MissingOutLinks(new AuthoritativeConcepts(new InvolvedConcepts())));
    }

    @Test
    public void testComponentsMissingOutLinkCount() throws OpenRDFException {
        Collection<Value> missingOutLinks = missingOutLinks1.getResult().getData();

        Assert.assertEquals(involvedConcepts.getResult().getData().size(), missingOutLinks.size());
    }

    @Test
    public void testConceptsMissingOutLinkCount() throws OpenRDFException {
        Collection<Value> missingOutLinks = missingOutLinks2.getResult().getData();

        Assert.assertEquals(7, missingOutLinks.size());
    }
}
