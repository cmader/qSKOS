package at.ac.univie.mminf.qskos4j.issues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class SkosReferenceIntegrityChecker extends Issue {

	private final Logger logger = LoggerFactory.getLogger(SkosReferenceIntegrityChecker.class);
	
	private List<Set<Resource>> stronglyConnectedComponents;
	
	public SkosReferenceIntegrityChecker(VocabRepository vocabRepository)
	{
		super(vocabRepository);
	}

	public CollectionResult<Pair<URI>> findAssociativeVsHierarchicalClashes(
		DirectedGraph<Resource, NamedEdge> hierarchyGraph) throws OpenRDFException
	{
		/* TODO: Algorithm
		 * 1. create hierarchy graph HG
		 * 2. compute all skos:related pairs (c1,c2)
		 * 3. compute strongly connected components of HG
		 * 4. if c1 and c2 are in same component => clash
		 * 5. else if c1 and c2 are in different components A and B
		 * 6. replace A and B with single nodes a and b
		 * 7. if a path a->b or b->a exists => clash
		 */
		Collection<Pair<URI>> clashes = new HashSet<Pair<URI>>();
		
		
		Collection<Pair<URI>> relatedConcepts = findRelatedConcepts();
		stronglyConnectedComponents = new StrongConnectivityInspector<Resource, NamedEdge>(hierarchyGraph).stronglyConnectedSets();
		for (Pair<URI> conceptPair : relatedConcepts) {
			try {
				if (inSameComponent(conceptPair)) {
					clashes.add(conceptPair);
				}
				else {
					//TODO: do something intelligent :)
				}
			}
			catch (NotInHierarchyGraphException e) {
				// one concepts in the pair is not in the hierarchy graph => can't be a clash
				continue;
			}
		}
		
		return null;
	}
	
	private Collection<Pair<URI>> findRelatedConcepts() throws OpenRDFException {
		TupleQueryResult result = vocabRepository.query(createRelatedConceptsQuery());
		return createResultCollection(result);
	}
	
	private String createRelatedConceptsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT DISTINCT ?concept1 ?concept2 WHERE {" +
				"?concept1 skos:related ?concept2 ." +
			"}";
	}
	
	private Collection<Pair<URI>> createResultCollection(TupleQueryResult result) 
		throws OpenRDFException
	{
		Collection<Pair<URI>> resultCollection = new ArrayList<Pair<URI>>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI concept1 = (URI) queryResult.getValue("concept1");
			URI concept2 = (URI) queryResult.getValue("concept2");
			
			resultCollection.add(new Pair<URI>(concept1, concept2));
		}
		
		return resultCollection;
	}
	
	private boolean inSameComponent(Pair<URI> concepts) {
		return getContainingComponent(concepts.getFirst()) == getContainingComponent(concepts.getSecond());
	}
	
	private Set<Resource> getContainingComponent(URI concept) {
		for (Set<Resource> component : stronglyConnectedComponents) {
			if (component.contains(concept)) {
				return component;
			}
		}
		
		throw new NotInHierarchyGraphException();
	}
		
	private Collection<Pair<URI>> findHierarchicallyConnectedConcepts(Collection<Pair<URI>> resourcePairs)
		throws OpenRDFException
	{
		Collection<Pair<URI>> relatedPairs = new ArrayList<Pair<URI>>();
		
		Iterator<Pair<URI>> it = new MonitoredIterator<Pair<URI>>(resourcePairs, progressMonitor);
		while (it.hasNext()) {
			Pair<URI> resourcePair = it.next();
			
			if (isHierarchicallyConnected(resourcePair)) {
				relatedPairs.add(resourcePair);
			}
		}
		
		return relatedPairs;
	}
	
	private boolean isHierarchicallyConnected(Pair<URI> resourcePair) 
		throws OpenRDFException
	{
		RepositoryConnection connection = vocabRepository.getRepository().getConnection();
		
		BooleanQuery graphBroaderQuery = connection.prepareBooleanQuery(
			QueryLanguage.SPARQL, 
			createRelatedBroaderQuery(resourcePair.getFirst(), resourcePair.getSecond()));		
		BooleanQuery graphNarrowerQuery = connection.prepareBooleanQuery(
			QueryLanguage.SPARQL, 
			createRelatedNarrowerQuery(resourcePair.getFirst(), resourcePair.getSecond()));
				
		try {
			return graphBroaderQuery.evaluate() || graphNarrowerQuery.evaluate();
		}
		catch (StackOverflowError e) {
			// occurs if the broader/narrower chain contains a cycle AND no path to the
			// target can be found. Also see test 7 in skosReferenceIntegrity.rdf
			logger.error("stack overflow querying repository; pair: " +resourcePair.toString());
			return false;
		}
	}
	
	private String createRelatedBroaderQuery(URI concept1, URI concept2) {
		return SparqlPrefix.SKOS+
			"ASK {" +
				"<"+concept1.stringValue()+"> (skos:broader|skos:broaderTransitive)+ <"+concept2.stringValue()+ ">." +
			"}";
	}
	
	private String createRelatedNarrowerQuery(URI concept1, URI concept2) {
		return SparqlPrefix.SKOS+
			"ASK {" +
				"<"+concept1.stringValue()+"> (skos:narrower|skos:narrowerTransitive)+ <"+concept2.stringValue()+ ">." +
			"}";		
	}
		
	public CollectionResult<Pair<URI>> findExactVsAssociativeMappingClashes()
		throws OpenRDFException
	{
		TupleQueryResult result = vocabRepository.query(createExVsAssMappingQuery());
		Collection<Pair<URI>> exactVsAssMappingClashes = createResultCollection(result);
		
		return new CollectionResult<Pair<URI>>(exactVsAssMappingClashes);
	}
	
	private String createExVsAssMappingQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?concept1 ?concept2 WHERE {" +
				"?concept1 skos:exactMatch ?concept2 ."+
				"?concept1 skos:broadMatch|skos:narrowMatch|skos:relatedMatch ?concept2 ." +
			"}";
	}
	
	@SuppressWarnings("serial")
	private class NotInHierarchyGraphException extends RuntimeException {
		
	}
	
}
