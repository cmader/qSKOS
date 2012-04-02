package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.criteria.AmbiguousRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ComponentFinder;
import at.ac.univie.mminf.qskos4j.criteria.ConceptFinder;
import at.ac.univie.mminf.qskos4j.criteria.ConceptSchemeChecker;
import at.ac.univie.mminf.qskos4j.criteria.HierarchyAnalyzer;
import at.ac.univie.mminf.qskos4j.criteria.InLinkFinder;
import at.ac.univie.mminf.qskos4j.criteria.InverseRelationsChecker;
import at.ac.univie.mminf.qskos4j.criteria.LanguageCoverageChecker;
import at.ac.univie.mminf.qskos4j.criteria.LanguageTagChecker;
import at.ac.univie.mminf.qskos4j.criteria.OutLinkFinder;
import at.ac.univie.mminf.qskos4j.criteria.RedundantAssociativeRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.RelationStatisticsFinder;
import at.ac.univie.mminf.qskos4j.criteria.ResourceAvailabilityChecker;
import at.ac.univie.mminf.qskos4j.criteria.SkosReferenceIntegrityChecker;
import at.ac.univie.mminf.qskos4j.criteria.SkosTermsChecker;
import at.ac.univie.mminf.qskos4j.criteria.SolitaryTransitiveRelationsFinder;
import at.ac.univie.mminf.qskos4j.criteria.UndocumentedConceptsChecker;
import at.ac.univie.mminf.qskos4j.criteria.ambiguouslabels.AmbiguousLabelFinder;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.LabelConflict;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.LabelConflictsFinder;
import at.ac.univie.mminf.qskos4j.result.custom.BrokenLinksResult;
import at.ac.univie.mminf.qskos4j.result.custom.ConceptLabelsResult;
import at.ac.univie.mminf.qskos4j.result.custom.IncompleteLangCovResult;
import at.ac.univie.mminf.qskos4j.result.custom.MissingLangTagResult;
import at.ac.univie.mminf.qskos4j.result.custom.UnidirRelConceptsResult;
import at.ac.univie.mminf.qskos4j.result.custom.WeaklyConnectedComponentsResult;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.general.NumberResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.progress.DummyProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class QSkos {

	private final Logger logger = LoggerFactory.getLogger(QSkos.class);
	
	private Set<String> sparqlEndPoints = new HashSet<String>();
	
	private ResourceAvailabilityChecker resourceAvailabilityChecker;	
	private HierarchyAnalyzer hierarchyAnalyer;
	private ConceptFinder conceptFinder;
	private ComponentFinder componentFinder;
	private RedundantAssociativeRelationsFinder redundantAssociativeRelationsFinder;
	private LanguageCoverageChecker languageCoverageChecker;
	
	private VocabRepository vocabRepository;
	private IProgressMonitor progressMonitor;
	private String authResourceIdentifier;
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
	
	public QSkos(File rdfFile,
		String baseURI,
		RDFFormat dataFormat) 
		throws OpenRDFException, IOException 
	{
		logger.info("initializing vocabulary from file '" +rdfFile.getName()+ "'...");
		
		vocabRepository = new VocabRepository(rdfFile, baseURI, dataFormat);
		
		extractAuthResourceIdentifier(baseURI);
		init();
	}
	
	private void init() {
		resourceAvailabilityChecker = new ResourceAvailabilityChecker(vocabRepository);
		hierarchyAnalyer = new HierarchyAnalyzer(vocabRepository);
		conceptFinder = new ConceptFinder(vocabRepository);
		componentFinder = new ComponentFinder(vocabRepository);
		redundantAssociativeRelationsFinder = new RedundantAssociativeRelationsFinder(vocabRepository);
		languageCoverageChecker = new LanguageCoverageChecker(vocabRepository);	
		
		progressMonitor = new DummyProgressMonitor();
	}
	
	private void extractAuthResourceIdentifier(String baseUri) {
		if (baseUri != null) {
			try {
				authResourceIdentifier = new java.net.URI(baseUri).getHost();
			} 
			catch (URISyntaxException e) {
				// cannot guess authoritative resource identifier
			}
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
		conceptFinder.setProgressMonitor(progressMonitor);
		
		if (authoritativeConcepts == null) {
			authoritativeConcepts = conceptFinder.findAuthoritativeConcepts(authResourceIdentifier);
			
			if (authResourceIdentifier == null) {
				authResourceIdentifier = conceptFinder.getAuthoritativeResourceIdentifier();
			}
			
		}
		return authoritativeConcepts;
	}
		
	public CollectionResult<URI> findOrphanConcepts() throws OpenRDFException
	{
		return conceptFinder.findOrphanConcepts();
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
	
	public CollectionResult<URI> findConceptSchemes() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findConceptSchemes();
	}
	
	public NumberResult<Long> findCollectionCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findCollectionCount();
	}
	
	public WeaklyConnectedComponentsResult findComponents() throws OpenRDFException {
		componentFinder.setProgressMonitor(progressMonitor);
		return componentFinder.findComponents(findInvolvedConcepts().getData());
	}
	
	public CollectionResult<Set<Resource>> findHierarchicalCycles() throws OpenRDFException {
		return hierarchyAnalyer.findCycleContainingComponents();
	}

	public void exportHierarchicalCyclesAsDOT(Writer[] writers) throws OpenRDFException {
		hierarchyAnalyer.exportCycleContainingComponents(writers);
	}

	public CollectionResult<URI> findMissingOutLinks() throws OpenRDFException {
		OutLinkFinder extResourcesFinder = new OutLinkFinder(vocabRepository);
		
		extResourcesFinder.setProgressMonitor(progressMonitor);
		return extResourcesFinder.findMissingOutLinks(
			findAuthoritativeConcepts().getData(),
			authResourceIdentifier);
	}
	
	public BrokenLinksResult findBrokenLinks() throws OpenRDFException 
	{
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findBrokenLinks(randomSubsetSize_percent, urlDereferencingDelay);
	}
	
	public CollectionResult<String> findInvalidResources(Float randomSubsetSize_percent) throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findInvalidResources(randomSubsetSize_percent, urlDereferencingDelay);
	}
		
	public CollectionResult<String> findNonHttpResources() throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findNonHttpResources();
	}
	
	public CollectionResult<URI> findUndefinedSkosResources() throws OpenRDFException {
		return new SkosTermsChecker(vocabRepository).findUndefinedSkosResources();
	}
		
	public MissingLangTagResult findOmittedOrInvalidLanguageTags() throws OpenRDFException {
		return new LanguageTagChecker(vocabRepository).findOmittedOrInvalidLanguageTags();
	}
	
	public IncompleteLangCovResult findIncompleteLanguageCoverage() throws OpenRDFException {
		languageCoverageChecker.setProgressMonitor(progressMonitor);
		return languageCoverageChecker.findIncompleteLanguageCoverage(findInvolvedConcepts().getData());
	}
	
	public ConceptLabelsResult findNotUniquePrefLabels() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findNotUniquePrefLabels();
	}
	
	public ConceptLabelsResult findNotDisjointLabels() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findNotDisjointLabels();
	}
	
	public CollectionResult<Pair<URI>> findValuelessAssociativeRelations() throws OpenRDFException {
		return redundantAssociativeRelationsFinder.findValuelessAssociativeRelations();
	}
	
	public CollectionResult<LabelConflict> findLabelConflicts() throws OpenRDFException {
		LabelConflictsFinder labelConflictsFinder = new LabelConflictsFinder(vocabRepository);
		labelConflictsFinder.setProgressMonitor(progressMonitor);
		return labelConflictsFinder.findLabelConflicts(findAuthoritativeConcepts().getData());
	}
	
	public CollectionResult<URI> findMissingInLinks() throws OpenRDFException 
	{
		InLinkFinder inLinkFinder = new InLinkFinder(
			vocabRepository, 
			sparqlEndPoints);
		inLinkFinder.setProgressMonitor(progressMonitor);
		return inLinkFinder.findMissingInLinks(findAuthoritativeConcepts().getData(), randomSubsetSize_percent);
	}
	
	public UnidirRelConceptsResult findOmittedInverseRelations() throws OpenRDFException {
		return new InverseRelationsChecker(vocabRepository).findOmittedInverseRelations();
	}
	
	public CollectionResult<Pair<URI>> findSolelyTransitivelyRelatedConcepts() throws OpenRDFException {
		return new SolitaryTransitiveRelationsFinder(vocabRepository).findSolelyTransitivelyRelatedConcepts();
	}
	
	public CollectionResult<Resource> findUndocumentedConcepts() throws OpenRDFException 
	{
		UndocumentedConceptsChecker docCovChecker = 
			new UndocumentedConceptsChecker(vocabRepository);
		docCovChecker.setProgressMonitor(progressMonitor);
		return docCovChecker.findUndocumentedConcepts(findInvolvedConcepts().getData());
	}
	
	public CollectionResult<URI> findOmittedTopConcepts() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findOmittedTopConcepts(findConceptSchemes().getData());
	}
	
	public CollectionResult<URI> findTopConceptsHavingBroaderConcepts() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findTopConceptsHavingBroaderConcepts();
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
	
	public CollectionResult<Pair<URI>> findAmbiguousRelations() throws OpenRDFException {
		return new AmbiguousRelationsFinder(vocabRepository).findAmbiguousRelations();
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
	public void addSparqlEndPoint(String endpointUrl) {
		sparqlEndPoints.add(endpointUrl);
	}
	
	public void setAuthoritativeResourceIdentifier(String authResourceIdentifier) {
		this.authResourceIdentifier = authResourceIdentifier;
	}
		
	public void setUrlDereferencingDelay(int delayMillis) {
		urlDereferencingDelay = delayMillis;
	}
	
	public void setSubsetSize(Float subsetSizePercent) {
		randomSubsetSize_percent = subsetSizePercent;
	}
	
	public void enableSkosXlSupport() throws OpenRDFException {
		logger.info("inferring SKOSXL triples");
		vocabRepository.enableSkosXlSupport();
	}
		
}
