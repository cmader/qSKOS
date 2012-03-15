package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import at.ac.univie.mminf.qskos4j.criteria.RelationStatisticsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ResourceAvailabilityChecker;
import at.ac.univie.mminf.qskos4j.criteria.SkosReferenceIntegrityChecker;
import at.ac.univie.mminf.qskos4j.criteria.SkosTermsChecker;
import at.ac.univie.mminf.qskos4j.criteria.SolitaryTransitiveRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ambiguouslabels.AmbiguousLabelFinder;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConcepts;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConceptsFinder;
import at.ac.univie.mminf.qskos4j.result.custom.AvgConceptIndegreeResult;
import at.ac.univie.mminf.qskos4j.result.custom.ConceptExtLinkAvgResult;
import at.ac.univie.mminf.qskos4j.result.custom.ConceptLabelsResult;
import at.ac.univie.mminf.qskos4j.result.custom.IllegalResourceResult;
import at.ac.univie.mminf.qskos4j.result.custom.IncomleteLangCovResult;
import at.ac.univie.mminf.qskos4j.result.custom.LinkTargetAvailabilityResult;
import at.ac.univie.mminf.qskos4j.result.custom.MissingLangTagResult;
import at.ac.univie.mminf.qskos4j.result.custom.RedundantAssocRelationsResult;
import at.ac.univie.mminf.qskos4j.result.custom.UnidirRelConceptsResult;
import at.ac.univie.mminf.qskos4j.result.custom.WeaklyConnectedComponentsResult;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.general.ConceptPairsResult;
import at.ac.univie.mminf.qskos4j.result.general.NumberResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.progress.DummyProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class QSkos {

	private final Logger logger = LoggerFactory.getLogger(QSkos.class);
	
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
	
	private CollectionResult<URI> involvedConcepts, authoritativeConcepts;
	
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
		logger.info("initializing vocabulary from SPARQL endpoint '" +queryEndpointUrl+ "'...");
		
		vocabRepository = new VocabRepository(queryEndpointUrl);
		init();
	}

	public QSkos(File rdfFile,
		String baseURI,
		RDFFormat dataFormat) 
		throws RepositoryException, RDFParseException, IOException 
	{
		logger.info("initializing vocabulary from file '" +rdfFile.getName()+ "'...");
		
		vocabRepository = new VocabRepository(rdfFile, baseURI, dataFormat);
		
		extractPublishingHost(baseURI);
		init();
	}
	
	private void init() {
		skosTermsChecker = new SkosTermsChecker(vocabRepository);
		resourceAvailabilityChecker = new ResourceAvailabilityChecker(vocabRepository);
		hierarchyAnalyer = new HierarchyAnalyzer(vocabRepository);
		conceptFinder = new ConceptFinder(vocabRepository);
		componentFinder = new ComponentFinder(vocabRepository);
		redundantAssociativeRelationsFinder = new RedundantAssociativeRelationsFinder(vocabRepository);
		languageCoverageChecker = new LanguageCoverageChecker(vocabRepository);	
		
		progressMonitor = new DummyProgressMonitor();
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
	
	public NumberResult<Long> getTripleCount() 
	{
		try {
			URI vocabContext = vocabRepository.getVocabContext();
			return new NumberResult<Long>(vocabRepository.getRepository().getConnection().size(vocabContext));
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	public CollectionResult<URI> findInvolvedConcepts() throws OpenRDFException
	{
		if (involvedConcepts == null) {
			involvedConcepts = conceptFinder.findInvolvedConcepts();
		}
		
		return involvedConcepts;
	}
	
	public CollectionResult<URI> findAuthoritativeConcepts() throws OpenRDFException {
		if (authoritativeConcepts == null) {		
			authoritativeConcepts = conceptFinder.getAuthoritativeConcepts(publishingHost, authoritativeUriSubstring);
		}
		
		return authoritativeConcepts;
	}
		
	public CollectionResult<URI> findLooseConcepts() throws OpenRDFException
	{
		return conceptFinder.findLooseConcepts();
	}
	
	public NumberResult<Long> findLexicalRelationsCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findLexicalRelationsCount(findInvolvedConcepts().getData());
	}
	
	public NumberResult<Long> findSemanticRelationsCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findSemanticRelationsCount();
	}
	
	public NumberResult<Long> findAggregationRelations() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findAggregationRelationsCount();
	}
	
	public NumberResult<Long> findConceptSchemeCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findConceptSchemeCount();
	}
	
	public NumberResult<Long> findCollectionCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findCollectionCount();
	}
	
	public WeaklyConnectedComponentsResult findComponents() throws OpenRDFException {
		return componentFinder.findComponents();
	}
	
	public void exportComponentsAsDOT(Writer[] writers) throws OpenRDFException {
		componentFinder.exportComponents(writers);
	}
	
	public CollectionResult<Set<URI>> findHierarchicalCycles() throws OpenRDFException {
		return hierarchyAnalyer.findCycleContainingComponents();
	}

	public void exportHierarchicalCyclesAsDOT(Writer[] writers) throws OpenRDFException {
		hierarchyAnalyer.exportCycleContainingComponents(writers);
	}

	public ConceptExtLinkAvgResult findExternalResources() throws OpenRDFException {
		ExternalResourcesFinder extResourcesFinder = new ExternalResourcesFinder(vocabRepository);
		
		extResourcesFinder.setProgressMonitor(progressMonitor);
		return extResourcesFinder.findExternalResourcesForConcepts(findInvolvedConcepts().getData(), publishingHost);
	}
	
	public LinkTargetAvailabilityResult checkResourceAvailability() throws OpenRDFException 
	{
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.checkResourceAvailability(randomSubsetSize_percent, urlDereferencingDelay);
	}
	
	public CollectionResult<String> findInvalidResources(Float randomSubsetSize_percent) throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findInvalidResources(randomSubsetSize_percent, urlDereferencingDelay);
	}
		
	public CollectionResult<String> findNonHttpResources() throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findNonHttpResources();
	}
	
	public IllegalResourceResult findDeprecatedProperties() throws OpenRDFException {
		return skosTermsChecker.findDeprecatedProperties();
	}
	
	public IllegalResourceResult findIllegalTerms() throws OpenRDFException {
		return skosTermsChecker.findIllegalTerms();
	}
	
	public MissingLangTagResult findMissingLanguageTags() throws OpenRDFException {
		return new LanguageTagChecker(vocabRepository).findMissingLanguageTags();
	}
	
	public IncomleteLangCovResult getIncompleteLanguageCoverage() throws OpenRDFException {
		languageCoverageChecker.setProgressMonitor(progressMonitor);
		return languageCoverageChecker.getIncompleteLanguageCoverage(findInvolvedConcepts().getData());
	}
	
	public ConceptLabelsResult findNotUniquePrefLabels() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findNotUniquePrefLabels();
	}
	
	public ConceptLabelsResult findNotDisjointLabels() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findNotDisjointLabels();
	}
	
	public RedundantAssocRelationsResult findRedundantAssociativeRelations() throws OpenRDFException {
		return redundantAssociativeRelationsFinder.findRedundantAssociativeRelations();
	}
	
	public CollectionResult<RelatedConcepts> findRelatedConcepts() throws OpenRDFException {
		RelatedConceptsFinder relatedConceptsFinder = new RelatedConceptsFinder(vocabRepository);
		relatedConceptsFinder.setProgressMonitor(progressMonitor);
		return relatedConceptsFinder.findRelatedConcepts(findInvolvedConcepts().getData());
	}
	
	public AvgConceptIndegreeResult analyzeConceptsRank() throws OpenRDFException 
	{
		ConceptRanker conceptRanker = new ConceptRanker(
			vocabRepository, 
			sparqlEndPoints);
		conceptRanker.setProgressMonitor(progressMonitor);
		return conceptRanker.analyzeConceptsRank(findAuthoritativeConcepts().getData(), randomSubsetSize_percent);
	}
	
	public UnidirRelConceptsResult findOmittedInverseRelations() throws OpenRDFException {
		return new InverseRelationsChecker(vocabRepository).findOmittedInverseRelations();
	}
	
	public ConceptPairsResult findSolitaryTransitiveRelations() throws OpenRDFException {
		return new SolitaryTransitiveRelationsFinder(vocabRepository).findSolitaryTransitiveRelations();
	}
	
	public NumberResult<Float> getAverageDocumentationCoverageRatio() throws OpenRDFException 
	{
		DocumentationCoverageChecker docCovChecker = 
			new DocumentationCoverageChecker(vocabRepository);
		docCovChecker.setProgressMonitor(progressMonitor);
		return docCovChecker.getAverageDocumentationCoverageRatio(findInvolvedConcepts().getData());
	}
	
	public CollectionResult<URI> findConceptSchemesWithoutTopConcept() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findConceptSchemesWithoutTopConcept();
	}
	
	public CollectionResult<URI> findTopConceptsHavingBroaderConcept() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findTopConceptsHavingBroaderConcept();
	}
	
	public CollectionResult<Pair<URI>> findAssociativeVsHierarchicalClashes() throws OpenRDFException {
		SkosReferenceIntegrityChecker skosReferenceIntegrityChecker = 
			new SkosReferenceIntegrityChecker(vocabRepository);
		skosReferenceIntegrityChecker.setProgressMonitor(progressMonitor);
		return skosReferenceIntegrityChecker.findAssociativeVsHierarchicalClashes();
	}
	
	public CollectionResult<Pair<URI>> findExactVsAssociativeMappingClashes() throws OpenRDFException {
		return new SkosReferenceIntegrityChecker(vocabRepository).findExactVsAssociativeMappingClashes();
	}
	
	public ConceptPairsResult findAmbiguousRelations() throws OpenRDFException {
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
