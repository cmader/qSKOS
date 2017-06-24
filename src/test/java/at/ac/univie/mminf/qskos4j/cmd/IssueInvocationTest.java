package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URL;

public class IssueInvocationTest {

    @Test
    public void testAllIssuesInvocation() {
        for (IssueDescriptor.IssueType issueType : IssueDescriptor.IssueType.values()) {
            invokeOnCmdLine(issueType);
        }
    }

    private void invokeOnCmdLine(IssueDescriptor.IssueType issueType) {
        String command = findCommand(issueType);
        String testFileName = getTestFileName();

        try {
            new VocEvaluate(new String[] {command, "-o", "/tmp/testreport", testFileName});
        }
        catch (Exception e) {
            Assert.fail(e.getMessage() + ", command: " +command);
        }
    }

    private String findCommand(IssueDescriptor.IssueType type) {
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
