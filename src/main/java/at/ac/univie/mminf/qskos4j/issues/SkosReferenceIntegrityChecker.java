package at.ac.univie.mminf.qskos4j.issues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class SkosReferenceIntegrityChecker extends Issue {

	public SkosReferenceIntegrityChecker(VocabRepository vocabRepository)
	{
		super(vocabRepository);
	}

	public CollectionResult<Pair<URI>> findAssociativeVsHierarchicalClashes(
		DirectedGraph<Resource, NamedEdge> hierarchyGraph) throws OpenRDFException
	{
		Collection<Pair<URI>> clashes = new HashSet<Pair<URI>>();
		
		Iterator<Pair<URI>> it = new MonitoredIterator<Pair<URI>>(
			findRelatedConcepts(), 
			progressMonitor);
		
		while (it.hasNext()) {
			Pair<URI> conceptPair = it.next();
			try {
				if (pathExists(hierarchyGraph, conceptPair)) {
					clashes.add(conceptPair);				
				}
			}
			catch (IllegalArgumentException e) {
				// one of the concepts not in graph, no clash possible
			}
		}
		
		return new CollectionResult<Pair<URI>>(clashes);
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
	
	private boolean pathExists(Graph<Resource, NamedEdge> hierarchyGraph, Pair<URI> conceptPair) {
		if (new DijkstraShortestPath<Resource, NamedEdge>(
			hierarchyGraph, 
			conceptPair.getFirst(), 
			conceptPair.getSecond()).getPathEdgeList() == null) 
		{
			return new DijkstraShortestPath<Resource, NamedEdge>(
				hierarchyGraph, 
				conceptPair.getSecond(), 
				conceptPair.getFirst()).getPathEdgeList() != null;
		}
		return true;
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
	
}
