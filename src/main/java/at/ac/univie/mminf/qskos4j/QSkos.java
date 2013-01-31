package at.ac.univie.mminf.qskos4j;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.UndocumentedConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.OmittedTopConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.TopConceptsHavingBroaderConcepts;
import at.ac.univie.mminf.qskos4j.issues.count.AggregationRelations;
import at.ac.univie.mminf.qskos4j.issues.count.SemanticRelations;
import at.ac.univie.mminf.qskos4j.issues.cycles.HierarchicalCycles;
import at.ac.univie.mminf.qskos4j.issues.inlinks.MissingInLinks;
import at.ac.univie.mminf.qskos4j.issues.labels.DisjointLabelsViolations;
import at.ac.univie.mminf.qskos4j.issues.labels.InconsistentPrefLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.LexicalRelations;
import at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.issues.language.IncompleteLanguageCoverage;
import at.ac.univie.mminf.qskos4j.issues.language.OmittedOrInvalidLanguageTags;
import at.ac.univie.mminf.qskos4j.issues.outlinks.BrokenLinks;
import at.ac.univie.mminf.qskos4j.issues.outlinks.HttpURIs;
import at.ac.univie.mminf.qskos4j.issues.outlinks.HttpUriSchemeViolations;
import at.ac.univie.mminf.qskos4j.issues.outlinks.MissingOutLinks;
import at.ac.univie.mminf.qskos4j.issues.relations.SolelyTransitivelyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.issues.relations.UnidirectionallyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.issues.relations.ValuelessAssociativeRelations;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.MappingClashes;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.UndefinedSkosResources;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Main class intended for easy interaction with qSKOS. On instantiation an in-memory ("local") repository 
 * containing the passed controlled vocabulary is created which can be queried by calling the methods of this class. 
 * 
 * @author christian
 *
 */
public class QSkos {

	private final Logger logger = LoggerFactory.getLogger(QSkos.class);

    /**
     * Delay to avoid flooding "external" sources. This is used, e.g., when dereferencing lots of links or sending
     * many queryies to a SPARQL endpoint
     */
    private final static int EXT_ACCESS_MILLIS = 1500;

	private VocabRepository vocabRepository;
	private String baseURI;
	private Integer extAccessDelayMillis = EXT_ACCESS_MILLIS;
	private Float randomSubsetSize_percent;
    private String authResourceIdentifier;

    private InvolvedConcepts involvedConcepts;
    private AuthoritativeConcepts authoritativeConcepts;
    private ConceptSchemes conceptSchemes;
    private HttpURIs httpURIs;

    private Collection<Issue> issuesToTest = new ArrayList<Issue>();
    private Collection<String> sparqlEndpointUrls = new ArrayList<String>();

    public QSkos(VocabRepository vocabRepository) {
        this.vocabRepository = vocabRepository;

        try {
            addAllIssues();
        }
        catch (OpenRDFException e) {
            logger.error("Error instantiating issue", e);
        }
    }

	private void addAllIssues() throws OpenRDFException {
        issuesToTest.clear();
        addStatisticalIssues();
        addAnalyticalIssues();
        addSkosIntegrityIssues();
    }

    private void addStatisticalIssues() {
        involvedConcepts = new InvolvedConcepts(vocabRepository);
        authoritativeConcepts = new AuthoritativeConcepts(involvedConcepts);
        authoritativeConcepts.setBaseURI(baseURI);
        authoritativeConcepts.setAuthResourceIdentifier(authResourceIdentifier);
        conceptSchemes = new ConceptSchemes(vocabRepository);
        httpURIs = new HttpURIs(vocabRepository);

        issuesToTest.add(involvedConcepts);
        issuesToTest.add(authoritativeConcepts);
        issuesToTest.add(new LexicalRelations(involvedConcepts));
        issuesToTest.add(new SemanticRelations(vocabRepository));
        issuesToTest.add(new AggregationRelations(vocabRepository));
        issuesToTest.add(conceptSchemes);
        issuesToTest.add(new at.ac.univie.mminf.qskos4j.issues.count.Collections(vocabRepository));
        issuesToTest.add(httpURIs);
    }

    private void addAnalyticalIssues() throws OpenRDFException {
        HierarchyGraphBuilder hierarchyGraphBuilder = new HierarchyGraphBuilder(vocabRepository);

        issuesToTest.add(new OmittedOrInvalidLanguageTags(vocabRepository));
        issuesToTest.add(new IncompleteLanguageCoverage(involvedConcepts));
        issuesToTest.add(new UndocumentedConcepts(authoritativeConcepts));
        issuesToTest.add(new OverlappingLabels(involvedConcepts));
        issuesToTest.add(new OrphanConcepts(involvedConcepts));
        issuesToTest.add(new DisconnectedConceptClusters(involvedConcepts));
        issuesToTest.add(new HierarchicalCycles(hierarchyGraphBuilder));
        issuesToTest.add(new ValuelessAssociativeRelations(vocabRepository));
        issuesToTest.add(new SolelyTransitivelyRelatedConcepts(vocabRepository));
        issuesToTest.add(new OmittedTopConcepts(conceptSchemes));
        issuesToTest.add(new TopConceptsHavingBroaderConcepts(vocabRepository));

        MissingInLinks missingInLinks = new MissingInLinks(authoritativeConcepts);
        missingInLinks.setQueryDelayMillis(extAccessDelayMillis);
        missingInLinks.setSubsetSize(randomSubsetSize_percent);
        for (String sparqlEndpointUrl : sparqlEndpointUrls) {
            missingInLinks.addSparqlEndPoint(sparqlEndpointUrl);
        }
        issuesToTest.add(missingInLinks);

        issuesToTest.add(new MissingOutLinks(authoritativeConcepts));

        BrokenLinks brokenLinks = new BrokenLinks(httpURIs);
        brokenLinks.setSubsetSize(randomSubsetSize_percent);
        brokenLinks.setExtAccessDelayMillis(extAccessDelayMillis);
        issuesToTest.add(brokenLinks);

        issuesToTest.add(new UndefinedSkosResources(vocabRepository));
        issuesToTest.add(new UnidirectionallyRelatedConcepts(vocabRepository));
        issuesToTest.add(new HttpUriSchemeViolations(vocabRepository));
        issuesToTest.add(new RelationClashes(hierarchyGraphBuilder));
        issuesToTest.add(new MappingClashes(vocabRepository));
    }

    private void addSkosIntegrityIssues() {
        ResourceLabelsCollector resourceLabelsCollector = new ResourceLabelsCollector(vocabRepository);

        issuesToTest.add(new InconsistentPrefLabels(resourceLabelsCollector));
        issuesToTest.add(new DisjointLabelsViolations(resourceLabelsCollector));
    }

    public Collection<Issue> getAllIssues() {
        return issuesToTest;
    }

	/**
	 * Set an IProgressMonitor that is notified on changes in the evaluation progress for every managed issues.
	 * @param progressMonitor monitor instance to be notified
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
        for (Issue issue : issuesToTest) {
            issue.setProgressMonitor(progressMonitor);
        }
	}
	
	/**
	 * Sets a delay time in milliseconds that must pass between accessing an external resource. This is intended to
     * avoid flooding of, e.g., vocabulary hosts or SPARQL endpoints with HTTP requests.
	 * 
	 * @param delayMillis delay time in milliseconds
	 */
	public void setExtAccessDelayMillis(int delayMillis) {
		extAccessDelayMillis = delayMillis;
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
     * Sets a string that is used to identify if an URI is authoritative. This is required to, e.g., find all
     * out-links to distinguish between URIs in the vocabulary namespace and other resources on the Web.
     *
     * @param authResourceIdentifier a string, usually a substring of an URI in the vocabulary's namespace,
     * that uniquely identifies an authoritative URI.
     */
    public void setAuthResourceIdentifier(String authResourceIdentifier) {
        this.authResourceIdentifier = authResourceIdentifier;
    }

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    public void addSparqlEndPoint(String endpointUrl) {
        sparqlEndpointUrls.add(endpointUrl);
    }

}