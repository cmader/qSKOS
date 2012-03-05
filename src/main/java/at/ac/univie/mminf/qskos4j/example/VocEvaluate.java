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
	
	private void outputMeasuresDescription() {
		String formatString = "%4s\t%-30s\t%s\n"; 
		System.out.format(formatString, "[ID]", "[Name]", "[Description]");
		for (CriterionDescription critDesc : CriterionDescription.values()) {
			System.out.format(formatString, critDesc.getId(), critDesc.getName(), critDesc.getDescription());
		}
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
		if (critDesc == CriterionDescription.WEAKLY_CONNECTED_COMPONENTS) {
			outputWccReport((Collection<Collection<URI>>) result);
		}
		else if (result instanceof Collection) {
			System.out.println("count: " +((Collection<?>) result).size());
		}
	}
	
	private void outputWccReport(Collection<Collection<URI>> result) {
		long componentCount = 0;
		
		for (Collection<URI> component : result) {
			componentCount += component.size() > 1 ? 1 : 0;
		}
		
		System.out.println("count: " +componentCount);
	}
	
	private void setupQSkos() throws OpenRDFException, IOException {
		qskos = new QSkos(new File(vocabFilenames.get(0)));
		qskos.setPublishingHost(publishingHost);
		qskos.setAuthoritativeUriSubstring(authoritativeUriSubstring);
		qskos.setProgressMonitor(new LoggingProgressMonitor());
		
		System.out.println("evaluating vocab: " +vocabFilenames.get(0));
	}
		
	private void findRedundantAssocicativeRelations() {
		Map<URI, Set<Pair<URI>>> redAssRels = qskos.findRedundantAssociativeRelations();
		dumpPairMap(redAssRels);
		
		Set<URI> involvedConcepts = new HashSet<URI>();
		for (Set<Pair<URI>> conceptPairs : redAssRels.values()) {
			involvedConcepts.addAll(getDistinctConceptsFromPairs(conceptPairs));	
		}
		
		System.out.println("concepts involved in redundant associative relations: "+involvedConcepts.size());
	}
	
	private void findAmbiguousRelations() {
		Set<Pair<URI>> ambiguousRelations = qskos.findAmbiguousRelations();
		
		Set<URI> distinctConcepts = getDistinctConceptsFromPairs(ambiguousRelations);
		System.out.println("Hierarchically and Associatively Related Concepts: " +distinctConcepts.size());
	}
	
	private void findOmittedInverseRelations() {
		Map<Pair<URI>, String> omittedInverseRelations = qskos.findOmittedInverseRelations();
		//System.out.println("omitted inverse relations: " +omittedInverseRelations.size());
		
		Set<URI> distinctConcepts = getDistinctConceptsFromPairs(omittedInverseRelations.keySet());
		System.out.println("Unidirectionally Related Concepts: " +distinctConcepts.size());		
	}
	
	private void findSolitaryTransitiveRelations() {
		Set<Pair<URI>> solitaryTransitiveRelations = qskos.findSolitaryTransitiveRelations();
		//System.out.println("solitary transitive relations: " +solitaryTransitiveRelations.size());
		
		Set<URI> distinctConcepts = getDistinctConceptsFromPairs(solitaryTransitiveRelations);
		System.out.println("Solely Transitively Related Concepts: " +distinctConcepts.size());
	}
	
	private void getExternalLinkAverage() {
		Map<URI, List<URL>> extResources = qskos.findExternalResources();
		
		List<URL> allExtUrls = new ArrayList<URL>();
		for (List<URL> extUrls : extResources.values()) {
			allExtUrls.addAll(extUrls);
		}
		
		float extLinkAvg = (float) allExtUrls.size() / (float) extResources.keySet().size();
		System.out.println("Concept External Link Average: " +extLinkAvg);
	}
	
	private void findNonHttpResources() {
		Set<String> nonHttpResources = qskos.findNonHttpResources();
		System.out.println("HTTP URI Scheme Violation: "+nonHttpResources.size());				
	}
	
	private void analyzeConceptsRank() {
		qskos.addSparqlEndPoint("http://sparql.sindice.com/sparql");
		Map<URI, Set<URI>> rankedConcepts = qskos.analyzeConceptsRank(randomSubsetSize_percent);
		dumpAvgConceptRank(rankedConcepts);
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
	
	private void checkSkosReferenceIntegrity() {
		Collection<Pair<URI>> assHierClashes = qskos.findAssociativeVsHierarchicalClashes();
		System.out.println("associative vs. hierarchical relation clashes: " +assHierClashes.size());
		Collection<Pair<URI>> exAssMapClashes = qskos.findExactVsAssociativeMappingClashes();
		System.out.println("exact vs. associative mapping clashes: " +exAssMapClashes.size());
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
	
	private void dumpAvailableResources(Map<URL, String> resourceAvailability)
	{
		long availableCount = 0, notAvailableCount = 0;
		
		for (URL url : resourceAvailability.keySet()) {
			String contentType = resourceAvailability.get(url);
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
		
		System.out.println("Link Target Unavailability");
		System.out.println("available: " +availableCount);
		System.out.println("not available: "+notAvailableCount);
	}
	
	private void dumpAvgConceptRank(Map<URI, Set<URI>> rankedConcepts)
	{
		long referencingResourcesCount = 0;
		/*
		for (Set<URI> referencingResources : rankedConcepts.values()) {
			referencingResourcesCount += referencingResources.size();
			if (!referencingResources.isEmpty()) {
				int i = 0;
				for (URI resource : referencingResources) {
					if (i < 10) {
						System.out.println(resource.stringValue());
					}
					else {
						System.out.println("...");
						break;
					}
					i++;
				}
			}
		}*/
		
		float avgConceptRank = 0;
		if (rankedConcepts.size() != 0) {
			avgConceptRank = (float) referencingResourcesCount / (float) rankedConcepts.size();
		}
		System.out.println("Average Concept In-degree: " +avgConceptRank);
	}
	
	private static void dumpPairMap(Map<URI, Set<Pair<URI>>> map) {
		System.out.println("Redundant Associative Relations Count:");
		if (map.isEmpty()) {
			System.out.println("empty");
		}
		else {
			/*
			URI firstKey = map.keySet().iterator().next();
			System.out.println(firstKey);
			System.out.println(map.get(firstKey));
			*/
			
			long results = 0;
			for (Set<Pair<URI>> pairs : map.values()) {
				results += pairs.size();
			}
			System.out.println("pair count: " +results);
		}
	}

}
