package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class RedundantAssociativeRelationsFinder extends Criterion {

	public RedundantAssociativeRelationsFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public Map<URI, Set<Pair<URI>>> findRedundantAssociativeRelations() throws OpenRDFException 
	{
		Map<URI, Set<Pair<URI>>> redundantAssociativeRelations = new HashMap<URI, Set<Pair<URI>>>();
		
		TupleQueryResult result = vocabRepository.query(createRedundantAssociativeRelationsQuery());
		generateResultsMap(redundantAssociativeRelations, result);
		
		return redundantAssociativeRelations;
	}
	
	public Map<URI, Set<Pair<URI>>> findNotAssociatedSiblings() throws OpenRDFException 
	{
		Map<URI, Set<Pair<URI>>> notAssociatedSiblings = new HashMap<URI, Set<Pair<URI>>>();
		
		TupleQueryResult result = vocabRepository.query(createNotAssociatedSiblingsQuery());
		generateResultsMap(notAssociatedSiblings, result);
		
		return notAssociatedSiblings;		
	}
	
	private String createRedundantAssociativeRelationsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?parent ?child ?otherchild "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"WHERE {?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?child . " +
				"?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?otherchild . " +
				"?child skos:related|skos:relatedMatch ?otherchild . }";
	}
	
	private String createNotAssociatedSiblingsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?parent ?child ?otherchild "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"WHERE {?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?child . " +
			"?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?otherchild . " +
			"FILTER (!sameTerm(?child, ?otherchild) && " +
				"NOT EXISTS {?child ?p ?otherchild} &&" +
				"NOT EXISTS {?otherchild ?p ?child})}";
	}
	
	private void generateResultsMap(Map<URI, Set<Pair<URI>>> map, TupleQueryResult result) 
		throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI parent = (URI) queryResult.getValue("parent");
			URI child = (URI) queryResult.getValue("child");
			URI otherchild = (URI) queryResult.getValue("otherchild");

			addToMap(map, parent, new Pair<URI>(child, otherchild));
		}
	}
	
	private void addToMap(
		Map<URI, Set<Pair<URI>>> map, 
		URI parent, 
		Pair<URI> resourcePair) 
	{
		Set<Pair<URI>> pairwiseRelatedConcepts = map.get(parent);
		
		if (pairwiseRelatedConcepts == null) {
			pairwiseRelatedConcepts = new HashSet<Pair<URI>>();
			map.put(parent, pairwiseRelatedConcepts);
		}
		
		pairwiseRelatedConcepts.add(resourcePair);
	}
	
}
