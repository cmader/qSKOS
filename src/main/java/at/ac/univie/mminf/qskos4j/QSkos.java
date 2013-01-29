package at.ac.univie.mminf.qskos4j;

import at.ac.univie.mminf.qskos4j.issues.*;
import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.OrphanConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.UndocumentedConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.OmittedTopConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.TopConceptsHavingBroaderConcepts;
import at.ac.univie.mminf.qskos4j.issues.count.*;
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
import at.ac.univie.mminf.qskos4j.issues.outlinks.MissingOutLinks;
import at.ac.univie.mminf.qskos4j.issues.outlinks.NonHttpResources;
import at.ac.univie.mminf.qskos4j.issues.relations.SolelyTransitivelyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.issues.relations.ValuelessAssociativeRelations;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.MappingClashes;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.RelationClashes;
import at.ac.univie.mminf.qskos4j.issues.skosintegrity.UndefinedSkosResources;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.event.PrintJobAttributeEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Collections;

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
	private IProgressMonitor progressMonitor;
	private String baseURI;
	private Integer extAccessDelayMillis = EXT_ACCESS_MILLIS;
	private Float randomSubsetSize_percent;
    private String authResourceIdentifier;
	
	private CollectionResult<URI> involvedConcepts, authoritativeConcepts;
	private DirectedMultigraph<Resource, NamedEdge> hierarchyGraph;
    private Collection<Issue> issuesToTest = Collections.EMPTY_LIST;
    private Collection<String> sparqlEndpointUrls = Collections.EMPTY_LIST;

	public void addAllIssues() throws OpenRDFException {
        issuesToTest.clear();
        HierarchyGraphBuilder hierarchyGraphBuilder = new HierarchyGraphBuilder(vocabRepository);
        ResourceLabelsCollector resourceLabelsCollector = new ResourceLabelsCollector(vocabRepository);

        InvolvedConcepts involvedConcepts = new InvolvedConcepts(vocabRepository);
        AuthoritativeConcepts authoritativeConcepts = new AuthoritativeConcepts(involvedConcepts);
        authoritativeConcepts.setBaseURI(baseURI);
        authoritativeConcepts.setAuthResourceIdentifier(authResourceIdentifier);
        HttpURIs httpURIs = new HttpURIs(vocabRepository);

        addIssue(involvedConcepts);
        addIssue(authoritativeConcepts);
        addIssue(new OrphanConcepts(involvedConcepts));
        addIssue(new MissingOutLinks(authoritativeConcepts));
        addIssue(new LexicalRelations(involvedConcepts));
        addIssue(new SemanticRelations(vocabRepository));
        addIssue(new AggregationRelations(vocabRepository));

        ConceptSchemes conceptSchemes = new ConceptSchemes(vocabRepository);
        addIssue(conceptSchemes);

        addIssue(new at.ac.univie.mminf.qskos4j.issues.count.Collections(vocabRepository));
        addIssue(httpURIs);
        addIssue(new DisconnectedConceptClusters(involvedConcepts));
        addIssue(new MissingOutLinks(authoritativeConcepts));
        addIssue(new HierarchicalCycles(hierarchyGraphBuilder));
        addIssue(new NonHttpResources(vocabRepository));
        addIssue(new OmittedOrInvalidLanguageTags(vocabRepository));
        addIssue(new IncompleteLanguageCoverage(involvedConcepts));
        addIssue(new InconsistentPrefLabels(resourceLabelsCollector));
        addIssue(new DisjointLabelsViolations(resourceLabelsCollector));
        addIssue(new OverlappingLabels(involvedConcepts));
        addIssue(new ValuelessAssociativeRelations(vocabRepository));
        addIssue(new UndefinedSkosResources(vocabRepository));
        addIssue(new UndocumentedConcepts(authoritativeConcepts));
        addIssue(new SolelyTransitivelyRelatedConcepts(vocabRepository));
        addIssue(new OmittedTopConcepts(conceptSchemes));
        addIssue(new TopConceptsHavingBroaderConcepts(vocabRepository));
        addIssue(new MappingClashes(vocabRepository));
        addIssue(new RelationClashes(hierarchyGraphBuilder));

        BrokenLinks brokenLinks = new BrokenLinks(httpURIs);
        brokenLinks.setSubsetSize(randomSubsetSize_percent);
        brokenLinks.setExtAccessDelayMillis(extAccessDelayMillis);
        addIssue(brokenLinks);

        MissingInLinks missingInLinks = new MissingInLinks(authoritativeConcepts);
        missingInLinks.setQueryDelayMillis(extAccessDelayMillis);
        missingInLinks.setSubsetSize(randomSubsetSize_percent);
        for (String sparqlEndpointUrl : sparqlEndpointUrls) {
            missingInLinks.addSparqlEndPoint(sparqlEndpointUrl);
        }
        addIssue(missingInLinks);

	}

    private void addIssue(Issue issue) {
        issuesToTest.add(issue);
        issue.setProgressMonitor(progressMonitor);
    }

    public Collection<Issue> getAllIssues() {
        return issuesToTest;
    }

	/**
	 * Set an IProgressMonitor that is notified on changes in the evaluation progress for every managed issues.
	 * @param progressMonitor monitor instance to be notified
	 */
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
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

    private void resetIssues() {
        for (Issue issue : issuesToTest) {
            issue.reset();
        }
    }

    public void setVocabRepository(VocabRepository vocabRepository) {
        this.vocabRepository = vocabRepository;
    }

    public void addSparqlEndPoint(String endpointUrl) {
        sparqlEndpointUrls.add(endpointUrl);
    }

}