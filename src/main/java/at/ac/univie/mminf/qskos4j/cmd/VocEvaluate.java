package at.ac.univie.mminf.qskos4j.cmd;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.openrdf.OpenRDFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.result.Result;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class VocEvaluate {
	
	private final Logger logger = LoggerFactory.getLogger(VocEvaluate.class);
	
	@Parameter(description = "vocabularyfile")
	private List<String> vocabFilenames;
	
	@Parameter(names = {"-h", "--publishing-host"}, description = "Name of publishing host")
	private String publishingHost;
	
	@Parameter(names = {"-a", "--auth-uri-substring"}, description = "Authoritative URI substring")
	private String authoritativeUriSubstring;
	
	@Parameter(names = {"-s", "--use-subset-percentage"}, description = "Use a specified percentage of the vocabulary triples for evaluation")
	private Float randomSubsetSize_percent;

	@Parameter(names = {"-l", "--list-measures"}, description = "Output a list of all available measure IDs")
	private Boolean outputMeasures;
	
	@Parameter(names = {"-m", "--use-measures"}, description = "Comma-separated list of measure IDs to perform")
	private String selectedCriteria;
	
	@Parameter(names = {"-e", "--extensive"}, description = "Output extensive report")
	private boolean extensiveReport = false;
	
	private QSkos qskos;
	
	public static void main(String[] args) {
		VocEvaluate instance = new VocEvaluate();
		JCommander jCommander = new JCommander(instance);
		
		try {
			jCommander.parse(args);
			instance.listMeasuresOrEvaluate();
		}
		catch (ParameterException e) {
			jCommander.usage();
		}
	}
	
	private void listMeasuresOrEvaluate() {
		if (outputMeasures != null && outputMeasures == true) {
			outputMeasuresDescription();
		}
		else {
			checkVocabFilenameGiven();
			evaluate();
		}
	}

	private void outputMeasuresDescription() {
		String formatString = "%5s\t%-55s\t%-60s\n"; 
		System.out.format(formatString, "[ID]", "[Name]", "[Description]");
		for (CriterionDescription critDesc : CriterionDescription.values()) {
			System.out.format(formatString, critDesc.getId(), critDesc.getName(), critDesc.getDescription());
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
			setupQSkos();
			performMeasures();
		} 
		catch (IOException ioException) {
			System.out.println("Error reading vocabulary file: " +ioException.getMessage());
		}
		catch (OpenRDFException rdfException) {
			System.out.println("Error processing vocabulary: " +rdfException.getMessage());
		}
	}
	
	private void setupQSkos() throws OpenRDFException, IOException {
		qskos = new QSkos(new File(vocabFilenames.get(0)));
		qskos.setPublishingHost(publishingHost);
		qskos.setAuthoritativeUriSubstring(authoritativeUriSubstring);
		qskos.setProgressMonitor(new LoggingProgressMonitor());
		qskos.setSubsetSize(randomSubsetSize_percent);
		qskos.addSparqlEndPoint("http://sparql.sindice.com/sparql");
		
		System.out.println("evaluating vocab: " +vocabFilenames.get(0));
	}

	private void performMeasures() {
		for (CriterionDescription criterion : extractCriteria()) {
			System.out.println("--- " +criterion.getName());
			String qSkosMethodName = criterion.getQSkosMethodName();
			
			try {
				Result<?> result = invokeQSkosMethod(qSkosMethodName);
				outputReport(result);
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
	
	private void outputReport(Result<?> result) {
		System.out.println(result.getShortReport());
		
		if (extensiveReport) {
			System.out.println(result.getExtensiveReport());
		}
	}
	
	private List<CriterionDescription> extractCriteria() {
		List<CriterionDescription> criteria = new ArrayList<CriterionDescription>();
		
		if (selectedCriteria == null) {
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
