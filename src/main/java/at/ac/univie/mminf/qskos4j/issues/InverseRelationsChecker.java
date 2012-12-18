package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.result.custom.UnidirRelResourcesResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.HashMap;
import java.util.Map;

public class InverseRelationsChecker extends Issue {

	private String[][] inversePropertyPairs = {
		{"skos:broader", "skos:narrower"}, 
		{"skos:broaderTransitive", "skos:narrowerTransitive"},
		{"skos:topConceptOf", "skos:hasTopConcept"},
		{"skos:narrowMatch", "skos:broadMatch"},
		{"skos:related", "skos:related"},
		{"skos:relatedMatch", "skos:relatedMatch"},
        {"skos:exactMatch", "skos:exactMatch"},
        {"skos:closeMatch", "skos:closeMatch"}
    };
	
	private Map<Pair<Resource>, String> omittedInverseRelations = new HashMap<Pair<Resource>, String>();

	public InverseRelationsChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public UnidirRelResourcesResult findUnidirectionallyRelatedConcepts() 
		throws OpenRDFException
	{
		for (String[] inversePropertyPair : inversePropertyPairs) {
			TupleQueryResult result = vocabRepository.query(createOmittedRelationsQuery(inversePropertyPair));
			addToOmittedReverseRelationsMap(result, inversePropertyPair);			
		}
		
		return new UnidirRelResourcesResult(omittedInverseRelations);
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
			Resource resource1 = (Resource) queryResult.getValue("resource1");
			Resource resource2 = (Resource) queryResult.getValue("resource2");
			
			omittedInverseRelations.put(
				new Pair<Resource>(resource1, resource2), 
				inversePropertyPair[0] +"/"+ inversePropertyPair[1]);
		}
	}

}
