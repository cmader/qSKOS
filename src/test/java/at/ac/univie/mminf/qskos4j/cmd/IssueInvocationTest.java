package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URL;

public class IssueInvocationTest {

	@Test
	public void testIssueInvocation() {
		for (Issue issue : new QSkos().getAllIssues()) {
			invokeOnCmdLine(issue);
		}
	}
	
	private void invokeOnCmdLine(Issue issue) {
		String command = findCommand(issue.getType());
		String testFileName = getTestFileName();
		
		try {
			new VocEvaluate(new String[] {command, "-c", issue.getId(), testFileName});
		}
		catch (Exception e) {
			Assert.fail();
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
