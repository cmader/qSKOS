package at.ac.univie.mminf.qskos4j.example;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.criteria.ExternalResourcesFinder;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConcepts;
import at.ac.univie.mminf.qskos4j.util.Pair;

public class VocEvaluate {
	
	private final Logger logger = LoggerFactory.getLogger(VocEvaluate.class);
	
	@Parameter(description = "vocabularyfile")
	private List<String> vocabFilenames;
	
	@Parameter(names = {"-h", "--publishing-host"}, description = "Publishing host")
	private String publishingHost;
	
	@Parameter(names = {"-a", "--auth-uri-substring"}, description = "Authoritative URI substring")
	private String authoritativeUriSubstring;
	
	@Parameter(names = {"-s", "--use-subset-percentage"}, description = "Use a specified percentage of the vocabulary triples for evaluation")
	private Float randomSubsetSize_percent;

	@Parameter(names = {"-l", "--list-measures"}, description = "Output a list of all available measures")
	private boolean outputMeasures;
	
	@Parameter(names = {"-m", "--use-measures"}, description = "Comma-separated list of measures to perform")
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
		if (outputMeasures) {
			outputMeasuresDescription();
		}
		else {
			checkVocabFilenameGiven();
			evaluate();
		}
	}

	private void outputMeasuresDescription() {
		String formatString = "%4s\t%-35s\t%s\n"; 
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
		/*	
			System.out.println("-- Graph-based criteria --");
			findConcepts();
			findComponents();
			findCycles();
			
			System.out.println("-- Structure-centric criteria --");
			findRedundantAssocicativeRelations();
			findAmbiguousRelations();
			findOmittedInverseRelations();
			findSolitaryTransitiveRelations();
			
			System.out.println("-- Linked Data criteria --");
			getExternalLinkAverage();
			findNonHttpResources();
			checkResourceAvailability();
			analyzeConceptsRank();
			
			System.out.println("-- SKOS-specific criteria --");
			checkSkosReferenceIntegrity();
			findIllegalTerms();
			findDeprecatedProperties();
			findConceptSchemesWithoutTopConcept();
			findTopConceptsHavingBroaderConcept();
			
			System.out.println("-- Labeling issues --");
			findMissingLangTags();
			getIncompleteLanguageCoverage();
			findAmbiguouslyLabeledConcepts();
			
			System.out.println("-- Other criteria --");
			findDocumentationCoverage();
			findRelatedConcepts();
		*/
	
	
	private List<CriterionDescription> extractCriteria() {
		List<CriterionDescription> criteria = new ArrayList<CriterionDescription>();
		
		StringTokenizer tokenizer = new StringTokenizer(selectedCriteria, ",");
		while (tokenizer.hasMoreElements()) {
			criteria.add(CriterionDescription.findById(tokenizer.nextToken()));
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
			
		case HTTP_URI_SCHEME_VIOLATION:
		case ASS_VS_HIER_RELATION_CLASHES:
		case EXACT_VS_ASS_MAPPING_CLASHES:
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
		System.out.println("count: " +((Collection<?>) result).size());
	}
		
	private void findIllegalTerms() {
		Map<URI, Set<URI>> illegalTerms = qskos.findIllegalTerms();
		System.out.println("illegal SKOS terms: " +illegalTerms.size());
	}
	
	private void findDeprecatedProperties() {
		Map<URI, Set<URI>> deprProps = qskos.findDeprecatedProperties();
		System.out.println("Deprecated property usage: " +deprProps.size());
	}
	
	private void findConceptSchemesWithoutTopConcept() {
		List<URI> conceptSchemes = qskos.findConceptSchemesWithoutTopConcept();
		System.out.println("Omitted Top Concepts: " +conceptSchemes.size());
	}
	
	private void findTopConceptsHavingBroaderConcept() {
		List<URI> topConcepts = qskos.findTopConceptsHavingBroaderConcept();
		System.out.println("Top Concepts Having Broader Concepts: " +topConcepts.size());	
	}
	
	private void findMissingLangTags() {
		Map<String, Set<Resource>> missingLangTags = qskos.findMissingLanguageTags();
		System.out.println("Language Tag Support: " +missingLangTags.keySet().size());
	}
	
	private void getIncompleteLanguageCoverage() {
		Map<Resource, Set<String>> incompleteLangCov = qskos.getIncompleteLanguageCoverage();
		System.out.println("Concepts With Incomplete Language Coverage: " +incompleteLangCov.size());
		
		/*
		Set<URI> conceptsMissingSpecificLanguage = new HashSet<URI>();
		
		for (URI concept : incompleteLangCov.keySet()) {
			Set<String> missingLangs = incompleteLangCov.get(concept);
			
			if (missingLangs.contains("en")) {
				conceptsMissingSpecificLanguage.add(concept);
			}
		}
		
		System.out.println("concepts missing english description: " +conceptsMissingSpecificLanguage.size());
		*/
	}
	
	private void findRelatedConcepts() {
		Set<RelatedConcepts> relatedConcepts = qskos.findRelatedConcepts();
		System.out.println("Unconnected Potentially Semantically Related Concepts: " +relatedConcepts.size());
	}
	
	private void findAmbiguouslyLabeledConcepts() {
		Map<URI, Set<String>> nonUniqPrefLabels = qskos.findNotUniquePrefLabels();
		System.out.println("not unique preflabels: " +nonUniqPrefLabels.size());
		
		Map<URI, Set<String>> notDisjointLabels = qskos.findNotDisjointLabels();
		System.out.println("not disjoint labels: " +notDisjointLabels.size());
	}
	
	private void findDocumentationCoverage() {
		float avgDocCovRatio = qskos.getAverageDocumentationCoverageRatio();
		System.out.println("Concept Documentation Coverage Ratio: " +avgDocCovRatio);

	}
		
	private void checkResourceAvailability() {
		Map<URL, String> resourceAvailability = qskos.checkResourceAvailability(randomSubsetSize_percent);
		dumpAvailableResources(resourceAvailability);
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
