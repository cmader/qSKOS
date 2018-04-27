package at.ac.univie.mminf.qskos4j.cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.rio.RDFFormat;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.ConsoleProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.StreamProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.vocab.InvalidRdfException;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

public class VocEvaluate {

	public final static String CMD_NAME_ANALYZE = "analyze";
	public final static String CMD_NAME_SUMMARIZE = "summarize";

	private static JCommander jc;
	private CommandSummarize parsedCommand;
	private QSkos qskos;
	private ReportCollector reportCollector;

	@Parameter(names = { "-v", "--version" }, description = "Outputs version of the tool")
	private boolean outputVersion = false;

	@Parameters(commandNames = CMD_NAME_SUMMARIZE, commandDescription = "Computes basic statistics of a given vocabulary")
	private class CommandSummarize {

		@SuppressWarnings("unused")
		@Parameter(description = "vocabularyfile")
		private List<String> vocabFilenames;

		@SuppressWarnings("unused")
		@Parameter(names = { "-a", "--auth-resource-identifier" }, description = "Authoritative resource identifier")
		private String authoritativeResourceIdentifier;

		@SuppressWarnings("unused")
		@Parameter(names = { "-c", "--check" }, description = "Comma-separated list of issue/statistics IDs to check for")
		private String selectedIds;

		@SuppressWarnings("unused")
		@Parameter(names = { "-dc", "--dont-check" }, description = "Comma-separated list of issue/statistics IDs NOT to check for")
		private String excludedIds;

		@Parameter(names = { "-xl", "--skosxl" }, description = "Enable SKOSXL support")
		private boolean enableSkosXl = false;

		@Parameter(names = { "-np", "--no-progress" }, description = "Suppresses output of a progress indicator")
		private boolean noProgressBar = false;

		@SuppressWarnings("unused")
		@Parameter(names = { "-d", "--debug" }, description = "Enable additional informative/debug output")
		private boolean debug;

		@SuppressWarnings("unused")
		@Parameter(names = { "-o", "--output" }, description = "Name of the file that holds the generated report")
		private String reportFileName;

		@SuppressWarnings("unused")
		@Parameter(names = { "-sf", "--stream-friendly" }, description = "Print the progress indicator in a stream-friendly format")
		private boolean streamFriendly;

		@SuppressWarnings("unused")
		@Parameter(names = { "-DQV", "--DataQualityVocabulary" }, description = "Print quality results as QualityMeasurement of W3C Data Quality Vocabulary")
		private boolean inDQV;

	}

	@Parameters(commandNames = CMD_NAME_ANALYZE, commandDescription = "Analyzes quality issues of a given vocabulary")
	private class CommandAnalyze extends CommandSummarize {

		@SuppressWarnings("unused")
		@Parameter(names = { "-sp", "--use-subset-percentage" }, description = "Use a specified percentage of the vocabulary triples for evaluation")
		private Float randomSubsetSize_percent;

		@Parameter(names = { "-wg", "--write-graphs" }, description = "Writes graphs as .dot files to current directory")
		private boolean writeGraphs = false;

	}

	public static void main(final String[] args) {
		try {
			new VocEvaluate(args);
		} catch (final ParameterException paramExc) {
			jc.usage();
			System.err.println("!! " + paramExc.getMessage());
		} catch (final IOException ioException) {
			System.err.println("!! Error reading file: "
					+ ioException.getMessage());
		} catch (final RDF4JException rdfException) {
			System.err.println("!! Error processing vocabulary: "
					+ rdfException.getMessage());
		}
	}

	public VocEvaluate(final String[] args) throws RDF4JException, IOException {
		this.qskos = new QSkos();
		parseCmdParams(args);

		if (this.outputVersion) {
			System.out.println("Version: "
					+ getClass().getPackage().getImplementationVersion());
		}

		if (this.parsedCommand == null) {
			jc.usage();
			return;
		}
		try {
			listIssuesOrEvaluate();
		} catch (final InvalidRdfException e) {
			System.err
					.println("!! Provided input file does not contain valid RDF data");
			System.exit(1);
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	private void parseCmdParams(final String[] args) {
		jc = new JCommander(this);

		final CommandAnalyze commandAnalyze = new CommandAnalyze();
		final CommandSummarize commandSummarize = new CommandSummarize();

		jc.addCommand(commandAnalyze);
		jc.addCommand(commandSummarize);
		jc.parse(args);

		final String command = jc.getParsedCommand();
		if (command != null) {
			if (command.equals(CMD_NAME_ANALYZE)) {
				this.parsedCommand = commandAnalyze;
			}
			if (command.equals(CMD_NAME_SUMMARIZE)) {
				this.parsedCommand = commandSummarize;
			}
		}
	}

	private void listIssuesOrEvaluate() throws RDF4JException, IOException {
		if (this.parsedCommand.vocabFilenames == null) {
			if (this.parsedCommand instanceof CommandAnalyze) {
				outputIssueDetails(IssueDescriptor.IssueType.ANALYTICAL);
			} else {
				outputIssueDetails(IssueDescriptor.IssueType.STATISTICAL);
			}
		} else {
			checkVocabFilenameGiven();
			evaluate();
		}
	}

	private void outputIssueDetails(
			final IssueDescriptor.IssueType constraintType) {
		for (final Issue issue : this.qskos.getAllIssues()) {
			final IssueDescriptor issueDescriptor = issue.getIssueDescriptor();

			if (issueDescriptor.getType() == constraintType) {
				System.out.println("---");
				System.out.println("ID: " + issueDescriptor.getId());
				System.out.println("Name: " + issueDescriptor.getName());
				System.out.println("Description: "
						+ issueDescriptor.getDescription());
				if (issueDescriptor.getWeblink() != null) {
					System.out.println("Further Informaton: <"
							+ issueDescriptor.getWeblink().toString() + ">");
				}
			}
		}
	}

	private void checkVocabFilenameGiven() throws ParameterException {
		if (this.parsedCommand.vocabFilenames == null) {
			throw new ParameterException("Please provide a vocabulary file");
		}
		if (this.parsedCommand.reportFileName == null) {
			throw new ParameterException("Please provide a report output file");
		}
	}

	private void evaluate() throws RDF4JException, IOException {
		setup();

		final String command = jc.getParsedCommand();
		String datasetAnalized = new String();
		for (final String s : this.parsedCommand.vocabFilenames) {
			datasetAnalized += s;
		}

		this.reportCollector = new ReportCollector(extractMeasures(),
				this.parsedCommand.reportFileName,
				this.parsedCommand.vocabFilenames,
				command.equals(CMD_NAME_ANALYZE), this.parsedCommand.inDQV,
				datasetAnalized);
		this.reportCollector.outputIssuesReport(shouldWriteGraphs());
	}

	private void setup() throws RDF4JException, IOException {
		setupLogging();

		final RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
		final File inputFile = new File(
				this.parsedCommand.vocabFilenames.get(0));
		final Repository repo = repositoryBuilder.setUpFromFile(inputFile,
				null, useRdfXmlFormatIfExtensionIsXml(inputFile));
		this.qskos.setRepositoryConnection(repo.getConnection());
		this.qskos
				.setAuthResourceIdentifier(this.parsedCommand.authoritativeResourceIdentifier);
		this.qskos.addSparqlEndPoint("http://sparql.sindice.com/sparql");
		this.qskos.addSparqlEndPoint("http://semantic.ckan.net/sparql");

		if (this.parsedCommand instanceof CommandAnalyze) {
			this.qskos
					.setSubsetSize(((CommandAnalyze) this.parsedCommand).randomSubsetSize_percent);
		}

		if (this.parsedCommand.enableSkosXl) {
			repositoryBuilder.enableSkosXlSupport();
		}

		if (!this.parsedCommand.noProgressBar) {
			if (this.parsedCommand.streamFriendly) {
				this.qskos.setProgressMonitor(new StreamProgressMonitor());
			} else {
				this.qskos.setProgressMonitor(new ConsoleProgressMonitor());
			}
		}
	}

	private RDFFormat useRdfXmlFormatIfExtensionIsXml(final File inputFile) {
		if (inputFile.getName().toLowerCase().endsWith(".xml")) {
			return RDFFormat.RDFXML;
		}
		return null;
	}

	private void setupLogging() {
		if (this.parsedCommand.debug) {
			System.setProperty("root-level", "DEBUG");
		}
	}

	private boolean shouldWriteGraphs() {
		return this.parsedCommand instanceof CommandAnalyze
				&& ((CommandAnalyze) this.parsedCommand).writeGraphs;
	}

	private Collection<Issue> extractMeasures() {
		Collection<Issue> resultingIssues;

		final Collection<Issue> selectedIssues = this.qskos
				.getIssues(this.parsedCommand.selectedIds);
		final Collection<Issue> excludedIssues = this.qskos
				.getIssues(this.parsedCommand.excludedIds);

		if (!selectedIssues.isEmpty()) {
			resultingIssues = selectedIssues;
		} else if (!excludedIssues.isEmpty()) {
			resultingIssues = getAllIssuesForCommand();
			resultingIssues.removeAll(excludedIssues);
		} else {
			resultingIssues = getAllIssuesForCommand();
		}

		return resultingIssues;
	}

	private Collection<Issue> getAllIssuesForCommand() {
		final List<Issue> issuesForCommand = new ArrayList<>();

		for (final Issue issue : this.qskos.getAllIssues()) {
			final String command = jc.getParsedCommand();

			final IssueDescriptor.IssueType issueType = issue
					.getIssueDescriptor().getType();

			if ((issueType == IssueDescriptor.IssueType.ANALYTICAL && command
					.equals(CMD_NAME_ANALYZE))
					|| (issueType == IssueDescriptor.IssueType.STATISTICAL && command
							.equals(CMD_NAME_SUMMARIZE))) {
				issuesForCommand.add(issue);
			}
		}

		return issuesForCommand;
	}

}