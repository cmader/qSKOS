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
	
	/**
	 * Finds all "authoritative concepts". See the <a href="https://github.com/cmader/qSKOS/blob/master/README.rdoc">
	 * qSKOS readme</a> for further information.
	 * 
	 * @throws OpenRDFException
	 */
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
		
	/**
	 * Further info on <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Orphan_Concepts">Orphan
	 * Concepts</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findOrphanConcepts() throws OpenRDFException
	{
		return conceptFinder.findOrphanConcepts();
	}
	
	/**
	 * Finds the number of relations involving SKOS lexical labels (prefLabel, altLabel, hiddenLabel). 
	 * 
	 * @throws OpenRDFException
	 */
	public NumberResult<Long> findLexicalRelationsCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findLexicalRelationsCount(findInvolvedConcepts().getData());
	}
	
	/**
	 * Finds the number of triples involving (subproperties of) skos:semanticRelation.
	 *
	 * @throws OpenRDFException
	 */
	public NumberResult<Long> findSemanticRelationsCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findSemanticRelationsCount();
	}
	
	/**
	 * Finds the number of triples that assign concepts to concept schemes or lists.
	 *
	 * @throws OpenRDFException
	 */
	public NumberResult<Long> findAggregationRelations() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findAggregationRelationsCount();
	}
	
	/**
	 * Finds the number of SKOS <a href="http://www.w3.org/TR/skos-reference/#schemes">
	 * ConceptSchemes</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findConceptSchemes() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findConceptSchemes();
	}
	
	/**
	 * Finds the number of SKOS <a href="http://www.w3.org/TR/skos-reference/#collections">
	 * Collections</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public NumberResult<Long> findCollectionCount() throws OpenRDFException
	{
		return new RelationStatisticsFinder(vocabRepository).findCollectionCount();
	}
	
	/**
	 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Weakly_Connected_Components">
	 * Weakly Connected Components</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public WeaklyConnectedComponentsResult findComponents() throws OpenRDFException {
		componentFinder.setProgressMonitor(progressMonitor);
		return componentFinder.findComponents(findInvolvedConcepts().getData());
	}
	
	/**
	 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Cyclic_Hierarchical_Relations">
	 * Cyclic Hierarchical Relations</a>.
	 * 
	 * @throws OpenRDFException
	 */
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
	
	/**
	 * Finds concepts without links to "external" resources (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_OutLinks">Missing Out-Links</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findMissingOutLinks() throws OpenRDFException {
		OutLinkFinder extResourcesFinder = new OutLinkFinder(vocabRepository);
		
		extResourcesFinder.setProgressMonitor(progressMonitor);
		return extResourcesFinder.findMissingOutLinks(
			findAuthoritativeConcepts().getData(),
			authResourceIdentifier);
	}
	
	/**
	 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Broken_Links">Broken Links</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public ExtrapolatedCollectionResult<URL> findBrokenLinks() throws OpenRDFException 
	{
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findBrokenLinks(randomSubsetSize_percent, urlDereferencingDelay);
	}
	
	/**
	 * Finds resources not within the HTTP URI scheme (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-HTTP_URI_Scheme_Violation">HTTP URI Scheme Violation</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<String> findNonHttpResources() throws OpenRDFException {
		resourceAvailabilityChecker.setProgressMonitor(progressMonitor);
		return resourceAvailabilityChecker.findNonHttpResources();
	}
	
	/**
	 * Finds resources not defined in the SKOS ontology (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Undefined_SKOS_Resources">Undefined SKOS Resources</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findUndefinedSkosResources() throws OpenRDFException {
		return new SkosTermsChecker(vocabRepository).findUndefinedSkosResources();
	}

	/**
	 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_or_Invalid_Language_Tags">
	 * Omitted or Invalid Language Tags</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public MissingLangTagResult findOmittedOrInvalidLanguageTags() throws OpenRDFException {
		return new LanguageTagChecker(vocabRepository).findOmittedOrInvalidLanguageTags();
	}
	
	/**
	 * Finds all concepts with incomplete language coverage (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Incomplete_Language_Coverage">Incomplete Language Coverage</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public IncompleteLangCovResult findIncompleteLanguageCoverage() throws OpenRDFException {
		languageCoverageChecker.setProgressMonitor(progressMonitor);
		return languageCoverageChecker.findIncompleteLanguageCoverage(findInvolvedConcepts().getData());
	}
	
	/**
	 * Finds concepts with more than one preferred label (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Ambiguously_Preflabeled_Concepts">Ambiguously Preflabeled Concepts</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public ConceptLabelsResult findAmbiguouslyPreflabeledConcepts() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findAmbiguouslyPreflabeledConcepts();
	}
	
	/**
	 * Finds concepts having identical entries for prefLabel, altLabel or hiddenLabel (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Disjoint_Labels_Violation-2">Disjoint Labels Violation</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public ConceptLabelsResult findDisjointLabelsViolations() throws OpenRDFException {
		return new AmbiguousLabelFinder(vocabRepository).findDisjointLabelsViolations();
	}
	
	/**
	 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<Pair<URI>> findValuelessAssociativeRelations() throws OpenRDFException {
		return redundantAssociativeRelationsFinder.findValuelessAssociativeRelations();
	}
	
	/**
	 * Finds concepts having the same preferred labels (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Label_Conflicts">Label Conflicts</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<LabelConflict> findLabelConflicts() throws OpenRDFException {
		LabelConflictsFinder labelConflictsFinder = new LabelConflictsFinder(vocabRepository);
		labelConflictsFinder.setProgressMonitor(progressMonitor);
		return labelConflictsFinder.findLabelConflicts(findAuthoritativeConcepts().getData());
	}
	
	/**
	 * Finds concepts that aren't referred by other vocabularies on the Web (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_InLinks">Missing In-Links</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findMissingInLinks() throws OpenRDFException 
	{
		InLinkFinder inLinkFinder = new InLinkFinder(
			vocabRepository, 
			otherRepositories);
		inLinkFinder.setProgressMonitor(progressMonitor);
		return inLinkFinder.findMissingInLinks(findAuthoritativeConcepts().getData(), randomSubsetSize_percent);
	}
	
	/**
	 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Unidirectionally_Related_Concepts">Unidirectionally Related Concepts</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public UnidirRelResourcesResult findUnidirectionallyRelatedConcepts() throws OpenRDFException {
		return new InverseRelationsChecker(vocabRepository).findUnidirectionallyRelatedConcepts();
	}
	
	/**
	 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Solely_Transitively_Related_Concepts">Solely Transitively Related Concepts</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<Pair<URI>> findSolelyTransitivelyRelatedConcepts() throws OpenRDFException {
		return new SolitaryTransitiveRelationsFinder(vocabRepository).findSolelyTransitivelyRelatedConcepts();
	}
	
	/**
	 * Finds concepts lacking documentation information (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Undocumented_Concepts">Undocumented Concepts</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<Resource> findUndocumentedConcepts() throws OpenRDFException 
	{
		UndocumentedConceptsChecker docCovChecker = 
			new UndocumentedConceptsChecker(vocabRepository);
		docCovChecker.setProgressMonitor(progressMonitor);
		return docCovChecker.findUndocumentedConcepts(findInvolvedConcepts().getData());
	}
	
	/**
	 * Finds concept schemes without top concepts (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_Top_Concepts">Omitted Top Concepts</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findOmittedTopConcepts() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findOmittedTopConcepts(findConceptSchemes().getData());
	}
	
	/**
	 * Finds top concepts that have broader concepts (
	 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Top_Concepts_Having_Broader_Concepts">Top Concepts Having Broader Concepts</a>
	 * ).
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<URI> findTopConceptsHavingBroaderConcepts() throws OpenRDFException {
		return new ConceptSchemeChecker(vocabRepository).findTopConceptsHavingBroaderConcepts();
	}
	
	/**
	 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Associative_vs_Hierarchical_Relation_Clashes">Associative vs. Hierarchical Relation Clashes</a>.
	 *
	 * @throws OpenRDFException
	 */
	public CollectionResult<Pair<URI>> findAssociativeVsHierarchicalClashes() throws OpenRDFException {
		SkosReferenceIntegrityChecker skosReferenceIntegrityChecker = new SkosReferenceIntegrityChecker(vocabRepository);
		skosReferenceIntegrityChecker.setProgressMonitor(progressMonitor);
		return skosReferenceIntegrityChecker.findAssociativeVsHierarchicalClashes(getHierarchyGraph());
	}
	
	/**
	 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Exact_vs_Associative_and_Hierarchical_Mapping_Clashes">Exact vs. Associative and Hierarchical Mapping Clashes</a>.
	 * 
	 * @throws OpenRDFException
	 */
	public CollectionResult<Pair<URI>> findExactVsAssociativeMappingClashes() throws OpenRDFException {
		return new SkosReferenceIntegrityChecker(vocabRepository).findExactVsAssociativeMappingClashes();
	}

	/**
	 * Set am IProgressMonitor that is notified on changes in the evaluation progress of Issues.
	 * @param progressMonitor
	 */
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