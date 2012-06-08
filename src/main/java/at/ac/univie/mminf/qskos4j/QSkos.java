package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.repository.Repository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.issues.ComponentFinder;
import at.ac.univie.mminf.qskos4j.issues.ConceptFinder;
import at.ac.univie.mminf.qskos4j.issues.ConceptSchemeChecker;
import at.ac.univie.mminf.qskos4j.issues.CycleFinder;
import at.ac.univie.mminf.qskos4j.issues.HierarchyGraph;
import at.ac.univie.mminf.qskos4j.issues.InLinkFinder;
import at.ac.univie.mminf.qskos4j.issues.InverseRelationsChecker;
import at.ac.univie.mminf.qskos4j.issues.LanguageCoverageChecker;
import at.ac.univie.mminf.qskos4j.issues.LanguageTagChecker;
import at.ac.univie.mminf.qskos4j.issues.OutLinkFinder;
import at.ac.univie.mminf.qskos4j.issues.RelationStatisticsFinder;
import at.ac.univie.mminf.qskos4j.issues.ResourceAvailabilityChecker;
import at.ac.univie.mminf.qskos4j.issues.SkosReferenceIntegrityChecker;
import at.ac.univie.mminf.qskos4j.issues.SkosTermsChecker;
import at.ac.univie.mminf.qskos4j.issues.SolitaryTransitiveRelationsFinder;
import at.ac.univie.mminf.qskos4j.issues.UndocumentedConceptsChecker;
import at.ac.univie.mminf.qskos4j.issues.ValuelessAssociativeRelationsFinder;
import at.ac.univie.mminf.qskos4j.issues.ambiguouslabels.AmbiguousLabelFinder;
import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabelConflict;
import at.ac.univie.mminf.qskos4j.issues.labelconflict.LabelConflictsFinder;
import at.ac.univie.mminf.qskos4j.result.custom.ConceptLabelsResult;
import at.ac.univie.mminf.qskos4j.result.custom.IncompleteLangCovResult;
import at.ac.univie.mminf.qskos4j.result.custom.MissingLangTagResult;
import at.ac.univie.mminf.qskos4j.result.custom.UnidirRelResourcesResult;
import at.ac.univie.mminf.qskos4j.result.custom.WeaklyConnectedComponentsResult;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.general.ExtrapolatedCollectionResult;
import at.ac.univie.mminf.qskos4j.result.general.NumberResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.progress.DummyProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

/**
 * Main class intended for easy interaction with qSKOS. On instantiation an in-memory ("local") repository 
 * containing the passed controlled vocabulary is created which can be queried by calling the methods of this class. 
 * 
 * @author christian
 *
 */
public class QSkos {

	private final Logger logger = LoggerFactory.getLogger(QSkos.class);
	
	private Collection<Repository> otherRepositories = new HashSet<Repository>();
	
	private ResourceAvailabilityChecker resourceAvailabilityChecker;	
	private ConceptFinder conceptFinder;
	private ComponentFinder componentFinder;
	private ValuelessAssociativeRelationsFinder redundantAssociativeRelationsFinder;
	private LanguageCoverageChecker languageCoverageChecker;
	
	private VocabRepository vocabRepository;
	private IProgressMonitor progressMonitor;
	private String authResourceIdentifier;
	private Integer urlDereferencingDelay;
	private Float randomSubsetSize_percent;
	
	private CollectionResult<URI> involvedConcepts, authoritativeConcepts;
	private DirectedMultigraph<Resource, NamedEdge> hierarchyGraph; 

	/**
	 * Constructs a QSkos object and initializes it with content from the passed RDF vocabulary.
	 * 
	 * @param rdfFile rdfFile a file holding a SKOS vocabulary
	 * @throws OpenRDFException if errors when initializing local repository 
	 * @throws IOException if problems occur reading the passed vocabulary file
	 */
	public QSkos(File rdfFile) 
		throws OpenRDFException, IOException
	{
		this(rdfFile, null, null);
	}
	
	/**
	 * Constructs a QSkos object and initializes it with content from the passed RDF vocabulary and
	 * explicitly stating the RDF serialization format.
	 * 
	 * @param rdfFile rdfFile a file holding a SKOS vocabulary
	 * @param dataFormat RDF serialization format of the passed vocabulary
	 * @throws OpenRDFException if errors when initializing temporal repository
	 * @throws IOException if problems occur reading the passed vocabulary file
	 */
	public QSkos(File rdfFile,
		RDFFormat dataFormat) 
		throws OpenRDFException, IOException
	{
		this(rdfFile, null, dataFormat);
	}
	
	private QSkos(File rdfFile,
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
		conceptFinder = new ConceptFinder(vocabRepository);
		componentFinder = new ComponentFinder(vocabRepository);
		redundantAssociativeRelationsFinder = new ValuelessAssociativeRelationsFinder(vocabRepository);
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
	
	/**
	 * Finds all <a href="http://www.w3.org/TR/skos-reference/#concepts">SKOS Concepts</a> involved in the vocabulary.
	 * 
	 * @return {@link CollectionResult} of all found concept URIs
	 * @throws OpenRDFException if querying the local repository fails
	 */
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
		return new CycleFinder(getHierarchyGraph()).findCycleContainingComponents();
	}
	
	private DirectedMultigraph<Resource, NamedEdge> getHierarchyGraph() 
		throws OpenRDFException
	{
		if (hierarchyGraph == null) {
			hierarchyGraph = new HierarchyGraph(vocabRepository).createGraph();
		}
		return hierarchyGraph;
	}
	
	public CollectionResult<URI> findMissingOutLinks() throws OpenRDFException {
		OutLinkFinder extResourcesFinder = new OutLinkFinder(vocabRepository);
		
		extResourcesFinder.setProgressMonitor(progressMonitor);
		return extResourcesFinder.findMissingOutLinks(
			findAuthoritativeConcepts().getData(),
			authResourceIdentifier);
	}
	
	public ExtrapolatedCollectionResult<URL> findBrokenLinks() throws OpenRDFException 
	{
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findBrokenLinks(randomSubsetSize_percent, urlDereferencingDelay);
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
			otherRepositories);
		inLinkFinder.setProgressMonitor(progressMonitor);
		return inLinkFinder.findMissingInLinks(findAuthoritativeConcepts().getData(), randomSubsetSize_percent);
	}
	
	public UnidirRelResourcesResult findOmittedInverseRelations() throws OpenRDFException {
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
		SkosReferenceIntegrityChecker skosReferenceIntegrityChecker = new SkosReferenceIntegrityChecker(vocabRepository);
		skosReferenceIntegrityChecker.setProgressMonitor(progressMonitor);
		return skosReferenceIntegrityChecker.findAssociativeVsHierarchicalClashes(getHierarchyGraph());
	}
	
	public CollectionResult<Pair<URI>> findExactVsAssociativeMappingClashes() throws OpenRDFException {
		return new SkosReferenceIntegrityChecker(vocabRepository).findExactVsAssociativeMappingClashes();
	}
		
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
	/**
	 * Adds a SPARQL endpoint for estimation of in-links.
	 * 
	 * @param endpointUrl SPARL endpoint URL
	 */
	public void addSparqlEndPoint(String endpointUrl) {
		otherRepositories.add(new SPARQLRepository(endpointUrl));
	}
	
	/**
	 * Adds the repository containing the vocabulary that's about to test to the list of
	 * other repositories. This is only useful for in-link testing purposes.
	 */
	public void addRepositoryLoopback() {
		otherRepositories.add(vocabRepository.getRepository());
	}
	
	/**
	 * Sets a string that is used to identify if an URI is authoritative. This is required to, e.g., find all
	 * out-links to distinguish between URIs in the vocabulary namespace and other resources on the Web.   
	 * 
	 * @param authResourceIdentifier a string, usually a substring of an URI in the vocabulary's namespace,
	 * that uniquely identifies an authoritative URI.
	 */
	public void setAuthoritativeResourceIdentifier(String authResourceIdentifier) {
		this.authResourceIdentifier = authResourceIdentifier;
	}
	
	/**
	 * Sets a delay time in milliseconds that must pass between dereferencing links. This is intended to avoid
	 * flooding the vocabulary host with HTTP requests.
	 * 
	 * @param delayMillis delay time in milliseconds
	 */
	public void setUrlDereferencingDelay(int delayMillis) {
		urlDereferencingDelay = delayMillis;
	}
	
	/**
	 * Some methods in this class support investigating only a subset of the vocabulary and extrapolate the results
	 * to shorten evaluation time. Works for, e.g., finding broken links. 
	 * 
	 * @param subsetSizePercent percentage of the total resources to investigate.
	 */
	public void setSubsetSize(Float subsetSizePercent) {
		randomSubsetSize_percent = subsetSizePercent;
	}
	
	/**
	 * If this is called, the local repository is complemented with SKOS lexical labels inferred from SKOSXL definitions 
	 * as described in the SKOS <a href="http://www.w3.org/TR/skos-reference/#S55">reference document</a> by the axioms
	 * S55-S57
	 * 
	 * @throws OpenRDFException if errors when initializing local repository
	 */
	public void enableSkosXlSupport() throws OpenRDFException {
		logger.info("inferring SKOSXL triples");
		vocabRepository.enableSkosXlSupport();
	}
		
}