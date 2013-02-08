package at.ac.univie.mminf.qskos4j.qskos;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.UnknownIssueIdException;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;
import java.util.*;

public class IssueIdTest extends QskosTestCase {

    private QSkos qskos;

    @Before
    public void setUp() throws IOException, OpenRDFException
    {
        qskos = new QSkos(setUpRepository("nocontent.rdf"));
    }

    @Test
    public void getAllIssuesByIdList() {
        String idList = getAllIdsAsCommaSeparatedList();

        try {
            qskos.getIssues(idList);
        }
        catch (UnknownIssueIdException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test(expected = UnknownIssueIdException.class)
    public void unknownIssueIdInListTest() {
        String idList = getAllIdsAsCommaSeparatedList() + ",bla";
        qskos.getIssues(idList);
    }

    @Test
    public void whiteSpacesInIssueIdListTest() {
        Iterator<Issue> issueIt = qskos.getAllIssues().iterator();
        String idList = issueIt.next().getId() +", "+ issueIt.next().getId();
        Collection<Issue> issues = qskos.getIssues(idList);
        Assert.assertEquals(2, issues.size());
    }

    @Test
    public void ensureUniqueIssueIdsTest() {
        List<Issue> allIssues = qskos.getAllIssues();
        Set<String> uniqueIssueIds = new HashSet<String>();
        for (Issue issue : allIssues) {
            uniqueIssueIds.add(issue.getId());
        }
        Assert.assertEquals(allIssues.size(), uniqueIssueIds.size());
    }

    private String getAllIdsAsCommaSeparatedList() {
        String idList = "";

        Iterator<Issue> issueIt = qskos.getAllIssues().iterator();
        while (issueIt.hasNext()) {
            idList += issueIt.next().getId() + (issueIt.hasNext() ? "," : "");
        }

        return idList;
    }

}
