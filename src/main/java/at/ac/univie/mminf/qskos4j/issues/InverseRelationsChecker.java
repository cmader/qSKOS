package at.ac.univie.mminf.qskos4j.issues;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.result.custom.UnidirRelConceptsResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class InverseRelationsChecker extends Criterion {

	private String[][] inversePropertyPairs = {
		{"skos:broader", "skos:narrower"}, 
		{"skos:broaderTransitive", "skos:narrowerTransitive"},
		{"skos:topConceptOf", "skos:hasTopConcept"},
		{"skos:narrowMatch", "skos:broadMatch"},
		{"skos:related", "skos:related"},
		{"skos:relatedMatch", "skos:relatedMatch"}};
	
	private Map<Pair<URI>, String> omittedInverseRelations = new HashMap<Pair<URI>, String>();
	private Set<Pair<URI>> inverseRelationPairs = new HashSet<Pair<URI>>();
	
	public InverseRelationsChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public UnidirRelConceptsResult findOmittedInverseRelations() 
		throws OpenRDFException
	{
		for (String[] inversePropertyPair : inversePropertyPairs) {
			TupleQueryResult result = vocabRepository.query(createOmittedRelationsQuery(inversePropertyPair));
			addToOmittedReverseRelationsMap(result, inversePropertyPair);			
		}
		
		return new UnidirRelConceptsResult(omittedInverseRelations);
	}
	
	private String createOmittedRelationsQuery(String[] inverseRelations) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource1 ?resource2 "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+

				"WHERE {" +
					"{?resource1 " +inverseRelations[0]+ " ?resource2 . "+
					"FILTER NOT EXISTS {?resource2 "+inverseRelations[1]+ " ?resource1}}" +
					"UNION" +
					"{?resource1 " +inverseRelations[1]+ " ?resource2 . "+
					"FILTER NOT EXISTS {?resource2 "+inverseRelations[0]+ " ?resource1}}" +
				"}";	
	}
	
	private void addToOmittedReverseRelationsMap(
		TupleQueryResult result, 
		String[] inversePropertyPair) throws QueryEvaluationException 
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI resource1 = (URI) queryResult.getValue("resource1");
			URI resource2 = (URI) queryResult.getValue("resource2");
			
			omittedInverseRelations.put(
				new Pair<URI>(resource1, resource2), 
				inversePropertyPair[0] +"/"+ inversePropertyPair[1]);
		}
	}
	
	/**
	 * Counts the number of resource pairs that are connected by a SKOS property
	 * that has an according inverse property. 
	 * @return The number of pairs found.
	 * @throws QueryEvaluationException 
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 */
	public long getInverseRelationPairCount() throws OpenRDFException {
		for (String[] inversePropertyPair : inversePropertyPairs) {
			for (String inverseProperty : inversePropertyPair) {
				TupleQueryResult result = vocabRepository.query(createInverseRelationQuery(inverseProperty));
				addToInverseRelationPairs(inverseRelationPairs, result);
			}
		}
		
		return inverseRelationPairs.size();
	}
	
	private String createInverseRelationQuery(String property) {
		return SparqlPrefix.SKOS + 
			"SELECT DISTINCT ?resource1 ?resource2 "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+
	
				"WHERE {" +
					"?resource1 "+property+"|^"+property+" ?resource2"+
				"}";
	}
	
	private void addToInverseRelationPairs(
		Collection<Pair<URI>> collection, 
		TupleQueryResult result) throws QueryEvaluationException 
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI resource1 = (URI) queryResult.getValue("resource1");
			URI resource2 = (URI) queryResult.getValue("resource2");
			
			inverseRelationPairs.add(new Pair<URI>(resource1, resource2));
		}
	}
	
}
