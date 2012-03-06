package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class SolitaryTransitiveRelationsFinder extends Criterion {

	private String[][] transitiveNontransiviteInverseProperties = {
			{"skos:broaderTransitive", "skos:broader", "skos:narrower"},
			{"skos:narrowerTransitive", "skos:narrower", "skos:broader"}};
	
	private Set<Pair<URI>> solitaryTransitiveRelations = new HashSet<Pair<URI>>();
	
	public SolitaryTransitiveRelationsFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public Set<Pair<URI>> findSolitaryTransitiveRelations()  
		throws OpenRDFException
	{
		for (String[] transitivePropertyPair : transitiveNontransiviteInverseProperties) {
			TupleQueryResult result = vocabRepository.query(createSolitaryTransitiveRelationsQuery(transitivePropertyPair));
			addToResults(result);			
		}
		
		return solitaryTransitiveRelations;
	}

	private String createSolitaryTransitiveRelationsQuery(
		String[] transitiveNontransiviteInverseProperties) 
	{
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource1 ?resource2 "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+

				"WHERE {" +
					"?resource1 " +transitiveNontransiviteInverseProperties[0]+ " ?resource2 . "+
					"FILTER NOT EXISTS {?resource1 "+transitiveNontransiviteInverseProperties[1]+ "|^"+transitiveNontransiviteInverseProperties[2]+" ?resource2}" +
					"}";
	}
	
	private void addToResults(TupleQueryResult result) 
		throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI resource1 = (URI) queryResult.getValue("resource1");
			URI resource2 = (URI) queryResult.getValue("resource2");
			
			solitaryTransitiveRelations.add(new Pair<URI>(resource1, resource2));
		}
	}
	
}
