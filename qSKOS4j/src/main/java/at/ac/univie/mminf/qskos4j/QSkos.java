package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.crypto.NoSuchMechanismException;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import at.ac.univie.mminf.qskos4j.criteria.AmbiguousRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ComponentFinder;
import at.ac.univie.mminf.qskos4j.criteria.ConceptFinder;
import at.ac.univie.mminf.qskos4j.criteria.ConceptRanker;
import at.ac.univie.mminf.qskos4j.criteria.ConceptSchemeChecker;
import at.ac.univie.mminf.qskos4j.criteria.DocumentationCoverageChecker;
import at.ac.univie.mminf.qskos4j.criteria.ExternalResourcesFinder;
import at.ac.univie.mminf.qskos4j.criteria.HierarchyAnalyzer;
import at.ac.univie.mminf.qskos4j.criteria.InverseRelationsChecker;
import at.ac.univie.mminf.qskos4j.criteria.LanguageCoverageChecker;
import at.ac.univie.mminf.qskos4j.criteria.LanguageTagChecker;
import at.ac.univie.mminf.qskos4j.criteria.RedundantAssociativeRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ResourceAvailabilityChecker;
import at.ac.univie.mminf.qskos4j.criteria.SkosReferenceIntegrityChecker;
import at.ac.univie.mminf.qskos4j.criteria.SkosTermsChecker;
import at.ac.univie.mminf.qskos4j.criteria.SolitaryTransitiveRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ambiguouslabels.AmbiguousLabelFinder;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConcepts;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConceptsFinder;
import at.ac.univie.mminf.qskos4j.util.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

@SuppressWarnings("unchecked")
public class QSkos {

	private Set<String> sparqlEndPoints = new HashSet<String>();
	
	private ResourceAvailabilityChecker resourceAvailabilityChecker;
	private SkosTermsChecker skosTermsChecker;
	private HierarchyAnalyzer hierarchyAnalyer;
	private ConceptFinder conceptFinder;
	private ComponentFinder componentFinder;
	private RedundantAssociativeRelationsFinder redundantAssociativeRelationsFinder;
	private LanguageCoverageChecker languageCoverageChecker;
	
	private VocabRepository vocabRepository;
	private IProgressMonitor progressMonitor;
	private String publishingHost, authoritativeUriSubstring;
	
	private Set<URI> involvedConcepts, authoritativeConcepts;
	
	public QSkos(File rdfFile) 
		throws RepositoryException, RDFParseException, IOException
	{
		this(rdfFile, null, null);
	}
	
	public QSkos(File rdfFile,
		RDFFormat dataFormat) 
		throws RepositoryException, RDFParseException, IOException
	{
		this(rdfFile, null, dataFormat);
	}
	
	public QSkos(File rdfFile,
		String baseURI)	
		throws RepositoryException, RDFParseException, IOException
	{
		this(rdfFile, baseURI, null);
	}

	public QSkos(File rdfFile,
		String baseURI,
		RDFFormat dataFormat) 
		throws RepositoryException, RDFParseException, IOException 
	{
		vocabRepository = new VocabRepository(rdfFile, baseURI, dataFormat);
		extractPublishingHost(baseURI);
		
		skosTermsChecker = new SkosTermsChecker(vocabRepository);
		resourceAvailabilityChecker = new ResourceAvailabilityChecker(vocabRepository);
		hierarchyAnalyer = new HierarchyAnalyzer(vocabRepository);
		conceptFinder = new ConceptFinder(vocabRepository);
		componentFinder = new ComponentFinder(vocabRepository);
		redundantAssociativeRelationsFinder = new RedundantAssociativeRelationsFinder(vocabRepository);
		languageCoverageChecker = new LanguageCoverageChecker(vocabRepository);				
	}
	
	private void extractPublishingHost(String baseUri) {
		if (baseUri == null) {
			publishingHost = null;
		}
		else {
			try {
				publishingHost = new java.net.URI(baseUri).getHost();
			} 
			catch (URISyntaxException e) {
				publishingHost = null;
			}
		}
	}
	
	public long getTripleCount() 
	{
		try {
			URI vocabContext = vocabRepository.getVocabContext();
			return vocabRepository.getRepository().getConnection().size(vocabContext);
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	public Set<URI> getInvolvedConcepts()
	{
		if (involvedConcepts == null) {
			involvedConcepts = (Set<URI>) invokeMethod(
				conceptFinder, 
				"getInvolvedConcepts", 
				false); 
		}
		
		return involvedConcepts;
	}
	
	public Set<URI> getAuthoritativeConcepts() {
		if (authoritativeConcepts == null) {		
			authoritativeConcepts = (Set<URI>) invokeMethod(
				conceptFinder, 
				"getAuthoritativeConcepts",
				publishingHost,
				authoritativeUriSubstring);
		}
		
		return authoritativeConcepts;
	}
		
	public Set<URI> findLooseConcepts()
	{
		return (Set<URI>) invokeMethod(
			conceptFinder, 
			"getInvolvedConcepts", 
			true);
	}
	
	public List<Set<URI>> findComponents() {
		return (List<Set<URI>>) invokeMethod(
			componentFinder,
			"findComponents");
	}
	
	public void exportComponentsAsDOT(Writer[] writers) {
		invokeMethod(
			componentFinder,
			"exportComponents", (Object) writers);
	}
	
	public List<Set<URI>> findHierarchicalCycles() {
		return (List<Set<URI>>) invokeMethod(
			hierarchyAnalyer,
			"findCycleContainingComponents");
	}

	public void exportHierarchicalCyclesAsDOT(Writer[] writers) {
		invokeMethod(
			hierarchyAnalyer,
			"exportCycleContainingComponents", (Object) writers);
	}

	public Map<URI, List<URL>> findExternalResources() {
		ExternalResourcesFinder extResourcesFinder = 
			new ExternalResourcesFinder(vocabRepository);
		extResourcesFinder.setProgressMonitor(progressMonitor);
			
		return (Map<URI, List<URL>>) invokeMethod(
			extResourcesFinder, 
			"findExternalResourcesForConcepts", 
			getInvolvedConcepts(),
			publishingHost);
	}
	
	public Map<URL, String> checkResourceAvailability() {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		
		return (Map<URL, String>) invokeMethod(
			resourceAvailabilityChecker, 
			"checkResourceAvailability",
			(Integer) null);
	}
	
	public Map<URL, String> checkResourceAvailability(Float randomSubsetSize_percent) 
	{
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		
		return (Map<URL, String>) invokeMethod(
			resourceAvailabilityChecker, 
			"checkResourceAvailability",
			randomSubsetSize_percent);
	}
	
	public Set<String> findInvalidResources(Float randomSubsetSize_percent) {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		
		return (Set<String>) invokeMethod(
			resourceAvailabilityChecker, 
			"findInvalidResources",
			randomSubsetSize_percent);
	}
		
	public Set<String> findNonHttpResources() {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		
		return (Set<String>) invokeMethod(
			resourceAvailabilityChecker, 
			"findNonHttpResources");	
	}
	
	public Map<URI, Set<URI>> findDeprecatedProperties() {
		return (Map<URI, Set<URI>>) invokeMethod(
			skosTermsChecker, 
			"findDeprecatedProperties");		
	}
	
	public Map<URI, Set<URI>> findIllegalTerms() {
		return (Map<URI, Set<URI>>) invokeMethod(
			skosTermsChecker, 
			"findIllegalTerms");				
	}
	
	public Map<String, Set<Resource>> findMissingLanguageTags() {
		return (Map<String, Set<Resource>>) invokeMethod(
			new LanguageTagChecker(vocabRepository), 
			"findMissingLanguageTags");						
	}
	
	public Map<URI, Set<String>> getIncompleteLanguageCoverage() {
		languageCoverageChecker.setProgressMonitor(progressMonitor);
		
		return (Map<URI, Set<String>>) invokeMethod(
			languageCoverageChecker, 
			"getIncompleteLanguageCoverage",
			getInvolvedConcepts());		
	}
	
	public Map<URI, Set<String>> findNotUniquePrefLabels() {
		return (Map<URI, Set<String>>) invokeMethod(
			new AmbiguousLabelFinder(vocabRepository), 
			"findNotUniquePrefLabels");
	}
	
	public Map<URI, Set<String>> findNotDisjointLabels() {
		return (Map<URI, Set<String>>) invokeMethod(
			new AmbiguousLabelFinder(vocabRepository), 
			"findNotDisjointLabels");
	}
	
	public Map<URI, Set<Pair<URI>>> findRedundantAssociativeRelations() {
		return (Map<URI, Set<Pair<URI>>>) invokeMethod(
			redundantAssociativeRelationsFinder, 
			"findRedundantAssociativeRelations");
	}
	
	public Map<URI, Set<Pair<URI>>> findNotAssociatedSiblings() {
		return (Map<URI, Set<Pair<URI>>>) invokeMethod(
			redundantAssociativeRelationsFinder, 
			"findNotAssociatedSiblings");
	}
	
	public Set<RelatedConcepts> findRelatedConcepts() {
		RelatedConceptsFinder relatedConceptsFinder = new RelatedConceptsFinder(vocabRepository);
		relatedConceptsFinder.setProgressMonitor(progressMonitor);
		
		return (Set<RelatedConcepts>) invokeMethod(
				relatedConceptsFinder, 
			"findRelatedConcepts",
			getInvolvedConcepts());
	}
	
	public Map<URI, Set<URI>> analyzeConceptsRank(Float randomSubsetSize_percent) 
	{
		ConceptRanker conceptRanker = new ConceptRanker(
			vocabRepository, 
			sparqlEndPoints);
		conceptRanker.setProgressMonitor(progressMonitor);

		return (Map<URI, Set<URI>>) invokeMethod(
			conceptRanker, 
			"analyzeConceptsRank",
			getAuthoritativeConcepts(),
			randomSubsetSize_percent);
	}
	
	public Map<Pair<URI>, String> findOmittedInverseRelations() {
		return (Map<Pair<URI>, String>) invokeMethod(
			new InverseRelationsChecker(vocabRepository), 
			"findOmittedInverseRelations");
	}
	
	public Set<Pair<URI>> findSolitaryTransitiveRelations() {
		return (Set<Pair<URI>>) invokeMethod(
			new SolitaryTransitiveRelationsFinder(vocabRepository), 
			"findSolitaryTransitiveRelations");		
	}
	
	public float getAverageDocumentationCoverageRatio() 
	{
		DocumentationCoverageChecker docCovChecker = 
			new DocumentationCoverageChecker(vocabRepository);
		docCovChecker.setProgressMonitor(progressMonitor);
		
		return (Float) invokeMethod(
			docCovChecker, 
			"getAverageDocumentationCoverageRatio",
			getInvolvedConcepts());
	}
	
	public List<URI> findConceptSchemesWithoutTopConcept() {
		return (List<URI>) invokeMethod(
			new ConceptSchemeChecker(vocabRepository), 
			"findConceptSchemesWithoutTopConcept");	
	}
	
	public List<URI> findTopConceptsHavingBroaderConcept() {
		return (List<URI>) invokeMethod(
			new ConceptSchemeChecker(vocabRepository), 
			"findTopConceptsHavingBroaderConcept");
	}
	
	public Collection<Pair<URI>> findAssociativeVsHierarchicalClashes() {
		SkosReferenceIntegrityChecker skosReferenceIntegrityChecker = 
			new SkosReferenceIntegrityChecker(vocabRepository);
		skosReferenceIntegrityChecker.setProgressMonitor(progressMonitor);
		
		return (Collection<Pair<URI>>) invokeMethod(
			skosReferenceIntegrityChecker, 
			"findAssociativeVsHierarchicalClashes");
	}
	
	public Collection<Pair<URI>> findExactVsAssociativeMappingClashes() {
		return (Collection<Pair<URI>>) invokeMethod(
			new SkosReferenceIntegrityChecker(vocabRepository), 
			"findExactVsAssociativeMappingClashes");
	}
	
	public Set<Pair<URI>> findAmbiguousRelations() {
		return (Set<Pair<URI>>) invokeMethod(
			new AmbiguousRelationsFinder(vocabRepository), 
			"findAmbiguousRelations");
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
	private <T> Object invokeMethod(T criterion, String methodName, Object... args) {
		try {
			for (Method method :  criterion.getClass().getMethods()) {
				if (method.getName().equals(methodName)) {
					return method.invoke(criterion, args);
				}
			}
			throw new NoSuchMechanismException("Method '" +methodName+ 
				"' not found in class '" +criterion.getClass().getName()+ "'");
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void addSparqlEndPoint(String endpointUrl) {
		sparqlEndPoints.add(endpointUrl);
	}
	
	public void setPublishingHost(String publishingHost) {
		this.publishingHost = publishingHost;
	}
	
	public void setAuthoritativeUriSubstring(String authoritativeUriSubstring) {
		this.authoritativeUriSubstring = authoritativeUriSubstring;
	}
		
}
