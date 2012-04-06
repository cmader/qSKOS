package at.ac.univie.mminf.qskos4j.cmd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.openrdf.OpenRDFException;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.cmd.CriterionDescription.MeasureType;
import at.ac.univie.mminf.qskos4j.result.Result;
import ch.qos.logback.classic.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class VocEvaluate {
	
	private Logger logger;
	
	@Parameter(description = "vocabularyfile")
	private List<String> vocabFilenames;
		
	@Parameter(names = {"-a", "--auth-resource-identifier"}, description = "Authoritative resource identifier")
	private String authoritativeResourceIdentifier;
	
	@Parameter(names = {"-sp", "--use-subset-percentage"}, description = "Use a specified percentage of the vocabulary triples for evaluation")
	private Float randomSubsetSize_percent;

	@Parameter(names = {"-li", "--list-issues"}, description = "Output a list of all available quality issue IDs")
	private Boolean outputIssues;
	
	@Parameter(names = {"-ls", "--list-statistics"}, description = "Output a list of all available statistics")
	private Boolean outputStatistics;
	
	@Parameter(names = {"-c", "--check-issue"}, description = "Comma-separated list of issue/statistics IDs to check for")
	private String selectedCriteria;
	
	@Parameter(names = {"-ca", "--check-all"}, description = "Checks for all available issues/statistics")
	private Boolean checkAll;

	@Parameter(names = {"-e", "--extensive"}, description = "Output extensive report")
	private boolean extensiveReport = false;
	
	@Parameter(names = {"-xl", "--skosxl"}, description = "Enable SKOSXL support")
	private boolean enableSkosXl = false;
	
	@Parameter(names = {"-wg", "--write-graphs"}, description = "Writes graphs as .dot files to current directory")
	private boolean writeGraphs = false;
	
	@Parameter(names = {"-q", "--quiet"}, description = "Suppress informative output")
	private boolean quiet = false;

	private QSkos qskos;
	
	public static void main(String[] args) {
		VocEvaluate instance = new VocEvaluate();
		JCommander jCommander = new JCommander(instance);

		try {
			jCommander.parse(args);
			instance.listIssuesOrEvaluate();
		}
		catch (ParameterException e) {
			jCommander.usage();
		}
	}
		
	private void listIssuesOrEvaluate() {
		if (outputIssues != null && outputIssues == true) {
			outputMeasureDescription(MeasureType.ISSUE);
		}
		else if (outputStatistics != null && outputStatistics == true) {
			outputMeasureDescription(MeasureType.STATISTICS);
		}
		else {
			checkVocabFilenameGiven();
			evaluate();
		}
	}

	private void outputMeasureDescription(MeasureType constraintType) {
		String formatString = "%5s\t%-55s\t%-60s\n"; 
		System.out.format(formatString, "[ID]", "[Name]", "[Description]");
		for (CriterionDescription critDesc : CriterionDescription.values()) {
			if (critDesc.getType() == constraintType) {
				System.out.format(formatString, critDesc.getId(), critDesc.getName(), critDesc.getDescription());
			}
		}
	}
	
	private void checkVocabFilenameGiven() throws ParameterException
	{
		if (vocabFilenames == null) {
			throw new ParameterException("No vocabulary file given");
		}		
	}
	
	private void evaluate() {
		try {		
			setup();
			checkForIssue();
		} 
		catch (IOException ioException) {
			System.out.println("Error reading vocabulary file: " +ioException.getMessage());
		}
		catch (OpenRDFException rdfException) {
			System.out.println("Error processing vocabulary: " +rdfException.getMessage());
		}
	}
	
	private void setup() throws OpenRDFException, IOException {
		setupLogging();

		qskos = new QSkos(new File(vocabFilenames.get(0)));
		qskos.setAuthoritativeResourceIdentifier(authoritativeResourceIdentifier);
		qskos.setProgressMonitor(new LoggingProgressMonitor());
		qskos.setSubsetSize(randomSubsetSize_percent);
		qskos.addSparqlEndPoint("http://sparql.sindice.com/sparql");
		
		if (enableSkosXl) {
			qskos.enableSkosXlSupport();
		}		
	}
	
	private void setupLogging() {
		if (quiet) {
			System.setProperty("root-level", "ERROR");	
		}		
		logger = (Logger) LoggerFactory.getLogger(VocEvaluate.class);
	}

	private void checkForIssue() {
		for (CriterionDescription criterion : extractCriteria()) {
			System.out.println("--- " +criterion.getName());
			String qSkosMethodName = criterion.getQSkosMethodName();
			
			try {
				Result<?> result = invokeQSkosMethod(qSkosMethodName);
				outputReport(result, criterion.getId());
			}
			catch (Exception e) {
				String message = e.getMessage();
				if (e instanceof InvocationTargetException) {
					message = ((InvocationTargetException) e).getTargetException().getMessage();
				}
				
				logger.error("error invoking method: " +message);
			}
		}
	}	
	
	private void outputReport(Result<?> result, String critId) {
		System.out.println(result.getShortReport());	

		if (extensiveReport) {
			System.out.println(result.getExtensiveReport());
		}
		
		if (writeGraphs) {
			try {
				Collection<String> dotGraph = result.getAsDOT();
				writeToFiles(dotGraph, critId);
			}
			catch (IOException e) {
				logger.error("error writing graph file for issue " +critId, e);
			}
		}
	}
	
	private void writeToFiles(Collection<String> dotGraphs, String fileName) 
		throws IOException
	{
		int i = 0;
		Iterator<String> it = dotGraphs.iterator();
		while (it.hasNext()) {
			String dotGraph = it.next();
			FileWriter writer = new FileWriter(new File(fileName +"_"+ i +".dot"));	
			writer.write(dotGraph);
			writer.close();
			i++;
		}
	}
	
	private List<CriterionDescription> extractCriteria() {
		List<CriterionDescription> criteria = new ArrayList<CriterionDescription>();
		
		if (checkAll != null && checkAll == true) {
			criteria = Arrays.asList(CriterionDescription.values());
		}
		else {		
			StringTokenizer tokenizer = new StringTokenizer(selectedCriteria, ",");
			while (tokenizer.hasMoreElements()) {
				criteria.add(CriterionDescription.findById(tokenizer.nextToken()));
			}
		}
		
		return criteria;
	}
	
	private Result<?> invokeQSkosMethod(String methodName) throws Exception {
		for (Method method : qskos.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				return (Result<?>) method.invoke(qskos);
			}
		}
		throw new NoSuchMethodException();
	}
	
}
