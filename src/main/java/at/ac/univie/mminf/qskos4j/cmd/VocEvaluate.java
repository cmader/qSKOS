package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VocEvaluate {
	
	public final static String CMD_NAME_ANALYZE = "analyze";
    public final static String CMD_NAME_SUMMARIZE = "summarize";
	
	private static JCommander jc;
	private CommandSummarize parsedCommand;
    private QSkos qskos;
    private ReportCollector reportCollector;
	
	@Parameter(names = {"-v", "--version"}, description = "Outputs version of the tool")
	private boolean outputVersion = false;
	
	@Parameters(commandNames = CMD_NAME_SUMMARIZE, commandDescription = "Computes basic statistics of a given vocabulary")
	private class CommandSummarize {

        @SuppressWarnings("unused")
		@Parameter(description = "vocabularyfile")
		private List<String> vocabFilenames;

        @SuppressWarnings("unused")
        @Parameter(names = {"-a", "--auth-resource-identifier"}, description = "Authoritative resource identifier")
		private String authoritativeResourceIdentifier;

        @SuppressWarnings("unused")
        @Parameter(names = {"-c", "--check"}, description = "Comma-separated list of issue/statistics IDs to check for")
		private String selectedIds;

        @SuppressWarnings("unused")
        @Parameter(names = {"-dc", "--dont-check"}, description = "Comma-separated list of issue/statistics IDs NOT to check for")
		private String excludedIds;

        @Parameter(names = {"-xl", "--skosxl"}, description = "Enable SKOSXL support")
		private boolean enableSkosXl = false;

        @Parameter(names = {"-np", "--no-progress"}, description = "Suppresses output of a progress indicator")
        private boolean noProgressBar = false;

        @SuppressWarnings("unused")
        @Parameter(names = {"-d", "--debug"}, description = "Enable additional informative/debug output")
        private boolean debug;

    }
	
	@Parameters(commandNames = CMD_NAME_ANALYZE, commandDescription = "Analyzes quality issues of a given vocabulary")
	private class CommandAnalyze extends CommandSummarize {

        @SuppressWarnings("unused")
        @Parameter(names = {"-sp", "--use-subset-percentage"}, description = "Use a specified percentage of the vocabulary triples for evaluation")
		private Float randomSubsetSize_percent;
	
		@Parameter(names = {"-e", "--extensive"}, description = "Output extensive report")
		private boolean extensiveReport = false;
		
		@Parameter(names = {"-wg", "--write-graphs"}, description = "Writes graphs as .dot files to current directory")
		private boolean writeGraphs = false;

        @SuppressWarnings("unused")
        @Parameter(
            names = {"-utf", "--uri-track-file"},
            description = "A file that contains concept URIs. The output will contain the issues in which each of the concepts shows up.")
        private String uriTrackFilename;
			
	}
	
	public static void main(String[] args) {
		try {
			new VocEvaluate(args);
		}
		catch (ParameterException paramExc) {
			jc.usage();
		}
		catch (IOException ioException) {
			System.out.println("Error reading file: " +ioException.getMessage());
		}
		catch (OpenRDFException rdfException) {
			System.out.println("Error processing vocabulary: " +rdfException.getMessage());
		} 
	}
		
	public VocEvaluate(String[] args) throws OpenRDFException, IOException
	{
		parseCmdParams(args);
		
		if (outputVersion) {
			System.out.println("Version: " +getClass().getPackage().getImplementationVersion());
		}
		
		if (parsedCommand == null) {
			jc.usage();
		}
		else {
			listIssuesOrEvaluate();
		}		
	}
	
	private void parseCmdParams(String[] args) {
		jc = new JCommander(this);
		
		CommandAnalyze commandAnalyze = new CommandAnalyze();
		CommandSummarize commandSummarize = new CommandSummarize();
		
		jc.addCommand(commandAnalyze);
		jc.addCommand(commandSummarize);		
		jc.parse(args);
		
		String command = jc.getParsedCommand();
		if (command != null) {			
			if (command.equals(CMD_NAME_ANALYZE)) {
				parsedCommand = commandAnalyze;
			}
			if (command.equals(CMD_NAME_SUMMARIZE)) {
				parsedCommand = commandSummarize;
			}
		}
	}
		
	private void listIssuesOrEvaluate() throws OpenRDFException, IOException
	{
		if (parsedCommand.vocabFilenames == null) {
			if (parsedCommand instanceof CommandAnalyze) {
				outputIssueDetails(Issue.IssueType.ANALYTICAL);
			}
			else {
				outputIssueDetails(Issue.IssueType.STATISTICAL);
			}
		}
		else {
			checkVocabFilenameGiven();
			evaluate();
		}
	}

	private void outputIssueDetails(Issue.IssueType constraintType) {
		for (Issue issue : qskos.getAllIssues()) {
			if (issue.getType() == constraintType) {
				System.out.println("---");
				System.out.println("ID: " +issue.getId());
				System.out.println("Name: " +issue.getName());
				System.out.println("Description: " +issue.getDescription());
			}
		}
	}
	
	private void checkVocabFilenameGiven() throws ParameterException
	{
		if (parsedCommand.vocabFilenames == null) {
			throw new ParameterException("No vocabulary file given");
		}		
	}
	
	private void evaluate() throws OpenRDFException, IOException
	{
		setup();

        reportCollector = new ReportCollector(extractMeasures());

        if (uriTrackingEnabled()) {
            reportCollector.outputURITrackingReport(new File(((CommandAnalyze) parsedCommand).uriTrackFilename));
        }
        else {
            reportCollector.outputIssuesReport(shouldOutputExtReport(), shouldWriteGraphs());
        }
	}
	
	private void setup() throws OpenRDFException, IOException {
        setupLogging();

        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repo = repositoryBuilder.setUpFromFile(new File(parsedCommand.vocabFilenames.get(0)), null, null);
		qskos = new QSkos();
        qskos.setRepositoryConnection(repo.getConnection());
		qskos.setAuthResourceIdentifier(parsedCommand.authoritativeResourceIdentifier);
		qskos.addSparqlEndPoint("http://sparql.sindice.com/sparql");
        qskos.addSparqlEndPoint("http://semantic.ckan.net/sparql");

        if (parsedCommand instanceof CommandAnalyze) {
			qskos.setSubsetSize(((CommandAnalyze) parsedCommand).randomSubsetSize_percent);
		}
		
		if (parsedCommand.enableSkosXl) {
            repositoryBuilder.enableSkosXlSupport();
		}

        if (!uriTrackingEnabled() && !parsedCommand.noProgressBar) {
            qskos.setProgressMonitor(new ConsoleProgressMonitor());
        }
    }

    private void setupLogging() {
        if (parsedCommand.debug) {
            System.setProperty("root-level", "DEBUG");
        }
    }

    private boolean uriTrackingEnabled() {
        return parsedCommand instanceof CommandAnalyze && ((CommandAnalyze) parsedCommand).uriTrackFilename != null;
    }
	
	private boolean shouldOutputExtReport() {
		return parsedCommand instanceof CommandAnalyze && ((CommandAnalyze) parsedCommand).extensiveReport;
	}
	
	private boolean shouldWriteGraphs() {
		return parsedCommand instanceof CommandAnalyze && ((CommandAnalyze) parsedCommand).writeGraphs;
	}
	
	private Collection<Issue> extractMeasures()
	{
		Collection<Issue> resultingIssues;

        Collection<Issue> selectedIssues = qskos.getIssues(parsedCommand.selectedIds);
        Collection<Issue> excludedIssues = qskos.getIssues(parsedCommand.excludedIds);

		if (!selectedIssues.isEmpty()) {
			resultingIssues = selectedIssues;
		}
		else if (!excludedIssues.isEmpty()) {
			resultingIssues = getAllIssuesForCommand();
			resultingIssues.removeAll(excludedIssues);
		}
		else {
			resultingIssues = getAllIssuesForCommand();
		}
		
		return resultingIssues;
	}
	
	private Collection<Issue> getAllIssuesForCommand() {
		List<Issue> issuesForCommand = new ArrayList<Issue>();
		
		for (Issue issue : qskos.getAllIssues()) {
			String command = jc.getParsedCommand();
			
			if ((issue.getType() == Issue.IssueType.ANALYTICAL && command.equals(CMD_NAME_ANALYZE)) ||
				(issue.getType() == Issue.IssueType.STATISTICAL && command.equals(CMD_NAME_SUMMARIZE)))
			{
				issuesForCommand.add(issue);
			}
		}
		
		return issuesForCommand;
	}

}