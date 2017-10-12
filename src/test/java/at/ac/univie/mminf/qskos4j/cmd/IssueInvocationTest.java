package at.ac.univie.mminf.qskos4j.cmd;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import junit.framework.Assert;

import org.junit.Test;

import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;

public class IssueInvocationTest {

	@Test
	public void testAllIssuesInvocation() {
		for (final IssueDescriptor.IssueType issueType : IssueDescriptor.IssueType
				.values()) {
			invokeOnCmdLine(issueType);
		}
	}

	private void invokeOnCmdLine(final IssueDescriptor.IssueType issueType) {
		final String command = findCommand(issueType);
		final String testFileName = getTestFileName();
		try {
			final File outputDirectory = Files.createTempFile("temp", "bla")
					.toFile();
			outputDirectory.setExecutable(true, true);
			outputDirectory.setWritable(true, true);
			outputDirectory.setReadable(true, true);
			new VocEvaluate(new String[] { command, "-o",
					outputDirectory.getCanonicalPath(), testFileName });
		} catch (final Exception e) {
			Assert.fail(e.getMessage() + ", command: " + command);
		}
	}

	private String findCommand(final IssueDescriptor.IssueType type) {
		switch (type) {
		case ANALYTICAL:
			return VocEvaluate.CMD_NAME_ANALYZE;

		case STATISTICAL:
			return VocEvaluate.CMD_NAME_SUMMARIZE;
		}

		throw new IllegalStateException("unknown measure type");
	}

	private String getTestFileName() {
		final URL conceptsUrl = getClass().getResource("/nocontent.rdf");
		return conceptsUrl.getFile();
	}

}
