package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URL;

public class IssueInvocationTest {

    @Test
    public void testAllIssuesInvocation() {
        for (Issue.IssueType issueType : Issue.IssueType.values()) {
            invokeOnCmdLine(issueType);
        }
    }

    private void invokeOnCmdLine(Issue.IssueType issueType) {
        String command = findCommand(issueType);
        String testFileName = getTestFileName();

        try {
            new VocEvaluate(new String[] {command, testFileName});
        }
        catch (Exception e) {
            Assert.fail(e.getMessage() + ", command: " +command);
        }
    }

    private String findCommand(Issue.IssueType type) {
        switch (type)
        {
            case ANALYTICAL:
                return VocEvaluate.CMD_NAME_ANALYZE;

            case STATISTICAL:
                return VocEvaluate.CMD_NAME_SUMMARIZE;
        }

        throw new IllegalStateException("unknown measure type");
    }

    private String getTestFileName() {
        URL conceptsUrl = getClass().getResource("/nocontent.rdf");
        return conceptsUrl.getFile();
    }

}
