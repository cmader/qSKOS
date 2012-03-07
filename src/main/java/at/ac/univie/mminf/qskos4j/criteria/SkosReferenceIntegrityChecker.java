package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class SkosReferenceIntegrityChecker extends Criterion {

	private final Logger logger = LoggerFactory.getLogger(SkosReferenceIntegrityChecker.class);
	
	public SkosReferenceIntegrityChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public CollectionResult<Pair<URI>> findAssociativeVsHierarchicalClashes() 
		throws OpenRDFException
	{
		TupleQueryResult result = vocabRepository.query(createRelatedConceptsQuery());
		Collection<Pair<URI>> relatedConcepts = createResultCollection(result);
		Collection<Pair<URI>> hierarchicallyConnectedConcepts = 
			findHierarchicallyConnectedConcepts(relatedConcepts);
		
		return new CollectionResult<Pair<URI>>(hierarchicallyConnectedConcepts);
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
	
}
