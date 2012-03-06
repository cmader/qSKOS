package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
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
import at.ac.univie.mminf.qskos4j.criteria.RelationStatisticsFinder;
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
	private Integer urlDereferencingDelay;
	private Float randomSubsetSize_percent;
	
	private Set<URI> involvedConcepts, authoritativeConcepts;
	
	public QSkos(File rdfFile) 
		throws OpenRDFException, IOException
	{
		this(rdfFile, null, null);
	}
	
	public QSkos(File rdfFile,
		RDFFormat dataFormat) 
		throws OpenRDFException, IOException
	{
		this(rdfFile, null, dataFormat);
	}
	
	public QSkos(File rdfFile,
		String baseURI)	
		throws OpenRDFException, IOException
	{
		this(rdfFile, baseURI, null);
	}
	
	public QSkos(String queryEndpointUrl) {
		vocabRepository = new VocabRepository(queryEndpointUrl);
		initCriteria();
	}

	public QSkos(File rdfFile,
		String baseURI,
		RDFFormat dataFormat) 
		throws RepositoryException, RDFParseException, IOException 
	{
		vocabRepository = new VocabRepository(rdfFile, baseURI, dataFormat);
		
		extractPublishingHost(baseURI);
		initCriteria();
	}
	
	private void initCriteria() {
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
	
	public Set<URI> getInvolvedConcepts() throws OpenRDFException
	{
		if (involvedConcepts == null) {
			involvedConcepts = conceptFinder.getInvolvedConcepts(false);
		}
		
		return involvedConcepts;
	}
	
	public Set<URI> getAuthoritativeConcepts() throws OpenRDFException {
		if (authoritativeConcepts == null) {		
			authoritativeConcepts = conceptFinder.getAuthoritativeConcepts(publishingHost, authoritativeUriSubstring);
		}
		
		return authoritativeConcepts;
	}
		
	public Set<URI> findLooseConcepts() throws OpenRDFException
	{
		return conceptFinder.getInvolvedConcepts(true);
	}
	
	public long findLexicalRelationsCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findLexicalRelationsCount(getInvolvedConcepts());
	}
	
	public long findSemanticRelationsCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findSemanticRelationsCount();
	}
	
	public long findAggregationRelations() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findAggregationRelationsCount();
	}
	
	public long findConceptSchemeCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findConceptSchemeCount();
	}
	
	public List<Set<URI>> findComponents() throws OpenRDFException {
		return componentFinder.findComponents();
	}
	
	public void exportComponentsAsDOT(Writer[] writers) throws OpenRDFException {
		componentFinder.exportComponents(writers);
	}
	
	public List<Set<URI>> findHierarchicalCycles() throws OpenRDFException {
		return hierarchyAnalyer.findCycleContainingComponents();
	}

	public void exportHierarchicalCyclesAsDOT(Writer[] writers) throws OpenRDFException {
		hierarchyAnalyer.exportCycleContainingComponents(writers);
	}

	public Map<URI, List<URL>> findExternalResources() throws OpenRDFException {
		ExternalResourcesFinder extResourcesFinder = new ExternalResourcesFinder(vocabRepository);
		
		extResourcesFinder.setProgressMonitor(progressMonitor);
		return extResourcesFinder.findExternalResourcesForConcepts(getInvolvedConcepts(), publishingHost);
	}
	
	public Map<URL, String> checkResourceAvailability() throws OpenRDFException 
	{
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.checkResourceAvailability(randomSubsetSize_percent, urlDereferencingDelay);
	}
	
	public Set<String> findInvalidResources(Float randomSubsetSize_percent) throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findInvalidResources(randomSubsetSize_percent, urlDereferencingDelay);
	}
		
	public Set<String> findNonHttpResources() throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findNonHttpResources();
	}
	
	public Map<URI, Set<URI>> findDeprecatedProperties() throws OpenRDFException {
		return skosTermsChecker.findDeprecatedProperties();
	}
	
	public Map<URI, Set<URI>> findIllegalTerms() throws OpenRDFException {
		return skosTermsChecker.findIllegalTerms();
	}
	
	public Map<String, Set<Resource>> findMissingLanguageTags() throws OpenRDFException {
		return new LanguageTagChecker(vocabRepository).findMissingLanguageTags();
	}
	
	public Map<Resource, Set<String>> getIncompleteLanguageCoverage() throws OpenRDFException {
		languageCoverageChecker.setProgressMonitor(progressMonitor);
		return languageCoverageChecker.getIncompleteLanguageCoverage(getInvolvedConcepts());
	}
	
	public Map<URI, Set<String>> findNotUniquePrefLabels() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findNotUniquePrefLabels();
	}
	
	public Map<URI, Set<String>> findNotDisjointLabels() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findNotDisjointLabels();
	}
	
	public Map<URI, Set<Pair<URI>>> findRedundantAssociativeRelations() throws OpenRDFException {
		return redundantAssociativeRelationsFinder.findRedundantAssociativeRelations();
	}
	
	public Map<URI, Set<Pair<URI>>> findNotAssociatedSiblings() throws OpenRDFException {
		return redundantAssociativeRelationsFinder.findNotAssociatedSiblings();
	}
	
	public Set<RelatedConcepts> findRelatedConcepts() throws OpenRDFException {
		RelatedConceptsFinder relatedConceptsFinder = new RelatedConceptsFinder(vocabRepository);
		relatedConceptsFinder.setProgressMonitor(progressMonitor);
		return relatedConceptsFinder.findRelatedConcepts(getInvolvedConcepts());
	}
	
	public Map<URI, Set<URI>> analyzeConceptsRank() throws OpenRDFException 
	{
		ConceptRanker conceptRanker = new ConceptRanker(
			vocabRepository, 
			sparqlEndPoints);
		conceptRanker.setProgressMonitor(progressMonitor);
		return conceptRanker.analyzeConceptsRank(getAuthoritativeConcepts(), randomSubsetSize_percent);
	}
	
	public Map<Pair<URI>, String> findOmittedInverseRelations() throws OpenRDFException {
		return new InverseRelationsChecker(vocabRepository).findOmittedInverseRelations();
	}
	
	public Set<Pair<URI>> findSolitaryTransitiveRelations() throws OpenRDFException {
		return new SolitaryTransitiveRelationsFinder(vocabRepository).findSolitaryTransitiveRelations();
	}
	
	public float getAverageDocumentationCoverageRatio() throws OpenRDFException 
	{
		DocumentationCoverageChecker docCovChecker = 
			new DocumentationCoverageChecker(vocabRepository);
		docCovChecker.setProgressMonitor(progressMonitor);
		return docCovChecker.getAverageDocumentationCoverageRatio(getInvolvedConcepts());
	}
	
	public List<URI> findConceptSchemesWithoutTopConcept() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findConceptSchemesWithoutTopConcept();
	}
	
	public List<URI> findTopConceptsHavingBroaderConcept() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findTopConceptsHavingBroaderConcept();
	}
	
	public Collection<Pair<URI>> findAssociativeVsHierarchicalClashes() throws OpenRDFException {
		SkosReferenceIntegrityChecker skosReferenceIntegrityChecker = 
			new SkosReferenceIntegrityChecker(vocabRepository);
		skosReferenceIntegrityChecker.setProgressMonitor(progressMonitor);
		return skosReferenceIntegrityChecker.findAssociativeVsHierarchicalClashes();
	}
	
	public Collection<Pair<URI>> findExactVsAssociativeMappingClashes() throws OpenRDFException {
		return new SkosReferenceIntegrityChecker(vocabRepository).findExactVsAssociativeMappingClashes();
	}
	
	public Set<Pair<URI>> findAmbiguousRelations() throws OpenRDFException {
		return new AmbiguousRelationsFinder(vocabRepository).findAmbiguousRelations();
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
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
	
	public void setUrlDereferencingDelay(int delayMillis) {
		urlDereferencingDelay = delayMillis;
	}
	
	public void setSubsetSize(Float subsetSizePercent) {
		randomSubsetSize_percent = subsetSizePercent;
	}
		
}
