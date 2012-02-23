package at.ac.univie.mminf.qskos4j.example;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConcepts;
import at.ac.univie.mminf.qskos4j.util.Pair;

public class VocEvaluate {

	private final String SUBSET_SIZE_PARAM = "-s",
						 PUBLISHING_HOST_PARAM = "-h",
						 AUTH_URI_PARAM = "-a";
	
	private String vocabFilename, publishingHost, authoritativeUriSubstring;
	private Float randomSubsetSize_percent;
	private QSkos qskos;
	
	private boolean checkArgs(String[] args) {
		if (args.length < 1 || args.length > 4) {
			return false;
		}
		
		vocabFilename = args[0];
		
		for (int i = 1; i < args.length; i++) {
			String parameter = args[i];
			
			if (parameter.toLowerCase().startsWith(SUBSET_SIZE_PARAM)) {
				extractSubsetPercentage(parameter);
			}
			else if (parameter.toLowerCase().contains(PUBLISHING_HOST_PARAM)) {
				publishingHost = extractParamValue(PUBLISHING_HOST_PARAM, parameter);
			}
			else if (parameter.toLowerCase().contains(AUTH_URI_PARAM)) {
				authoritativeUriSubstring = extractParamValue(AUTH_URI_PARAM, parameter);
			}
		}
		return true;
	}
	
	private void extractSubsetPercentage(String parameterString) {
		String percentageSubString = extractParamValue(SUBSET_SIZE_PARAM, parameterString);
		
		try  {
			randomSubsetSize_percent = Float.parseFloat(percentageSubString);
		}
		catch (NumberFormatException e) {
			randomSubsetSize_percent = null;
		}	
	}
	
	private String extractParamValue(String paramIdentifier, String allParams) {
		int subsetSizeIndex = allParams.indexOf(paramIdentifier) + paramIdentifier.length();
		return allParams.substring(subsetSizeIndex);
	}
	
	private void evaluate() {
		try {
			setupQSkos();
			
			System.out.println("-- Graph-based criteria --");
			findConcepts();
			/*findComponents();
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
			findRelatedConcepts();*/
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setupQSkos() throws Exception {
		qskos = new QSkos(new File(vocabFilename));
		qskos.setPublishingHost(publishingHost);
		qskos.setAuthoritativeUriSubstring(authoritativeUriSubstring);
		qskos.setProgressMonitor(new LoggingProgressMonitor());
		
		System.out.println("evaluating vocab: " +vocabFilename);
	}
	
	private void findConcepts() {
		Set<URI> concepts = qskos.getInvolvedConcepts();
		System.out.println("total concepts: " +concepts.size());
		
		if (publishingHost != null || authoritativeUriSubstring != null) {
			System.out.println("authoritative concepts: " +qskos.getAuthoritativeConcepts().size());
		}
		
		Set<URI> looseConcepts = qskos.findLooseConcepts();
		System.out.println("loose concepts: " +looseConcepts.size());
	}
	
	private void findComponents() {
		List<Set<URI>> components = qskos.findComponents();
		
		long componentCount = 0;
		for (Set<URI> component : components) {
			componentCount += component.size() > 1 ? 1 : 0;
		}
		
		System.out.println("Weakly Connected Components: " +componentCount);
	}
	
	private void findCycles() {
		List<Set<URI>> cycleSets = qskos.findHierarchicalCycles();
		System.out.println("Hierarchical Cycles Containing Components: " +cycleSets.size());
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
		Map<URI, Set<String>> incompleteLangCov = qskos.getIncompleteLanguageCoverage();
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
	
	public static void main(String[] args) {
		VocEvaluate instance = new VocEvaluate();
		
		if (instance.checkArgs(args)) {
			instance.evaluate();
		}
		else {
			System.out.println("Usage: VocEvaluate vocabFilename [-hPublishingHost] [-aAuthoritativeUriSubstring] [-sPercentage]" +
				"\nDescription:\n" +
				"To achieve more accurate metrics (e.g, to find external links), parameter -h or -a can be passed"+
				"\n  " +
				"-h: Provide the name of the vocabulary's original host. Will be guessed if missing."+
				"\n  " +
				"-a: Provide a substring that is common for all resources in the scope of the provided vocabulary"+
				"\n\n  " +
				"-s: use a random sample subset of the given percentage of the vocabulary data");
		}						
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
