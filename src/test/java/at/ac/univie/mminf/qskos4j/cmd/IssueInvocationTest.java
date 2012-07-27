package at.ac.univie.mminf.qskos4j.cmd;

import java.net.URL;

import at.ac.univie.mminf.qskos4j.util.measureinvocation.MeasureDescription;
import junit.framework.Assert;

import org.junit.Test;

import at.ac.univie.mminf.qskos4j.util.measureinvocation.MeasureDescription.MeasureType;

public class IssueInvocationTest {

	@Test
	public void testIssueInvocation() {
		for (MeasureDescription desc : MeasureDescription.values()) {
			invokeOnCmdLine(desc);
		}
	}
	
	private void invokeOnCmdLine(MeasureDescription desc) {
		String command = findCommand(desc.getType());
		String testFileName = getTestFileName();
		
		try {
			new VocEvaluate(new String[] {command, "-c", desc.getId(), testFileName});
		}
		catch (Exception e) {
			Assert.fail();
		}
	}
	
	private String findCommand(MeasureType type) {
		switch (type)
		{
		case ISSUE:
			return VocEvaluate.CMD_NAME_ANALYZE;
			
		case STATISTICS:
			return VocEvaluate.CMD_NAME_SUMMARIZE;
		}
		
		throw new IllegalStateException("unknown measure type");
	}
	
	private String getTestFileName() {
		URL conceptsUrl = getClass().getResource("/nocontent.rdf");
		return conceptsUrl.getFile();
	}
	
}
