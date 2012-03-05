package at.ac.univie.mminf.qskos4j.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.Pair;

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
		String formatString = "%4s\t%-50s\t%s\n"; 
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
				Object result = invokeQSkosMethod(qSkosMethodName);
				outputReport(result, criterion);
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
	
	private Object invokeQSkosMethod(String methodName) throws Exception {
		for (Method method : qskos.getClass().getMethods()) {
			if (method.getName().equals(methodName)) {
				return method.invoke(qskos);
			}
		}
		throw new NoSuchMethodException();
	}
	
	@SuppressWarnings("unchecked")
	private void outputReport(Object result, CriterionDescription critDesc) {
		switch (critDesc) {
		
		case WEAKLY_CONNECTED_COMPONENTS:
			outputWccReport((Collection<Collection<URI>>) result);
			break;
			
		case REDUNDANT_ASSOCIATIVE_RELATIONS:
			outputRarReport((Map<URI, Set<Pair<URI>>>) result);
			break;
			
		case HIERARCHICALLY_AND_ASSOCIATIVELY_RELATED_CONCEPTS:
		case SOLELY_TRANSITIVELY_RELATED_CONCEPTS:
			outputSetOfPairsReport((Set<Pair<URI>>) result);
			break;
			
		case UNIDIRECTIONALLY_RELATED_CONCEPTS:
			outputSetOfPairsReport(((Map<Pair<URI>, String>) result).keySet());
			break;
			
		case CONCEPT_EXT_LINK_AVG:
			outputCelaReport((Map<URI, List<URL>>) result);
			break;
			
		case LINK_TARGET_AVAILABILITY:
			outputLtaReport((Map<URL, String>) result);
			break;
			
		case AVG_CONCEPT_INDEGREE:
			outputAciReport((Map<URI, Set<URI>>) result);
			break;
			
		case ILLEGAL_TERMS:
		case DEPRECATED_PROP_USAGE:
		case MISSING_LANG_TAGS:
			outputStandardReport(((Map<URI, Set<URI>>) result).keySet());
			break;
			
		case CONCEPTS_INCOMPLETE_LANG_COVERAGE:
		case AMBIGUOUS_PREFLABELED_CONCEPTS:
		case NOT_DISJOINT_LABELED_CONCEPTS:
			outputStandardReport(((Map<Resource, Set<String>>) result).keySet());
			break;
			
		case HTTP_URI_SCHEME_VIOLATION:
		case ASS_VS_HIER_RELATION_CLASHES:
		case EXACT_VS_ASS_MAPPING_CLASHES:
		case OMITTED_TOP_CONCEPTS:
		case TOP_CONCEPTS_HAVING_BROADER:
		case SEM_RELATED_CONCEPTS:
		case AVG_DOC_COVERAGE:
		default:
			outputStandardReport(result);
		}		
	}
	
	private void outputWccReport(Collection<Collection<URI>> result) {
		long componentCount = 0;
		
		for (Collection<URI> component : result) {
			componentCount += component.size() > 1 ? 1 : 0;
		}
		
		System.out.println("count: " +componentCount);
	}
	
	private void outputRarReport(Map<URI, Set<Pair<URI>>> result) {
		long pairCount = 0;
		
		if (!result.isEmpty()) {			
			for (Set<Pair<URI>> pairs : result.values()) {
				pairCount += pairs.size();
			}
			System.out.println("pair count: " +pairCount);
		}
		
		Set<URI> involvedConcepts = new HashSet<URI>();
		for (Set<Pair<URI>> conceptPairs : result.values()) {
			involvedConcepts.addAll(getDistinctConceptsFromPairs(conceptPairs));	
		}
		
		System.out.println("concept count: "+involvedConcepts.size());
	}
	
	private void outputSetOfPairsReport(Set<Pair<URI>> result) {
		Set<URI> distinctConcepts = getDistinctConceptsFromPairs(result);
		System.out.println("concept count: " +distinctConcepts.size());		
	}
		
	private void outputCelaReport(Map<URI, List<URL>> result) {
		List<URL> allExtUrls = new ArrayList<URL>();
		for (List<URL> extUrls : result.values()) {
			allExtUrls.addAll(extUrls);
		}
		
		float extLinkAvg = (float) allExtUrls.size() / (float) result.keySet().size();
		System.out.println("value: " +extLinkAvg);
	}
	
	private void outputLtaReport(Map<URL, String> result) {
		long availableCount = 0, notAvailableCount = 0;
		
		for (URL url : result.keySet()) {
			String contentType = result.get(url);
			if (contentType == null) {
				notAvailableCount++;
				System.out.println(url.toString());
			}
			else {
				availableCount++;
			}
		}
		
		if (randomSubsetSize_percent != null) {
			availableCount *= 100 / randomSubsetSize_percent;
			notAvailableCount *= 100 / randomSubsetSize_percent;
		}
		
		System.out.println("available: " +availableCount);
		System.out.println("not available: "+notAvailableCount);
	}
	
	private void outputAciReport(Map<URI, Set<URI>> result) {
		long referencingResourcesCount = 0;
		
		float avgConceptRank = 0;
		if (result.size() != 0) {
			avgConceptRank = (float) referencingResourcesCount / (float) result.size();
		}
		System.out.println("value: " +avgConceptRank);
	}
	
	private void outputStandardReport(Object result) {
		if (result instanceof Collection) {
			System.out.println("count: " +((Collection<?>) result).size());
		}
		else if (result instanceof Number) {
			System.out.println("value: " +result.toString());
		}
	}
						
	private Set<URI> getDistinctConceptsFromPairs(Set<Pair<URI>> pairs) {
		Set<URI> distinctConcepts = new HashSet<URI>();
		
		for (Pair<URI> pair : pairs) {
			distinctConcepts.add(pair.getFirst());
			distinctConcepts.add(pair.getSecond());
		}
		
		return distinctConcepts;
	}
		
}
