package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Solely_Transitively_Related_Concepts">Solely Transitively Related Concepts</a>.
 */
public class SolelyTransitivelyRelatedConcepts extends Issue<CollectionResult<Pair<URI>>> {

	private String[][] transitiveNontransiviteInverseProperties = {
			{"skos:broaderTransitive", "skos:broader", "skos:narrower"},
			{"skos:narrowerTransitive", "skos:narrower", "skos:broader"}};
	
	private Set<Pair<URI>> solitaryTransitiveRelations = new HashSet<Pair<URI>>();

    public SolelyTransitivelyRelatedConcepts(VocabRepository vocabRepo) {
        super(vocabRepo,
              "strc",
              "Solely Transitively Related Concepts",
              "Concepts only related by skos:broaderTransitive or skos:narrowerTransitive",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionResult<Pair<URI>> invoke() throws OpenRDFException {
		for (String[] transitivePropertyPair : transitiveNontransiviteInverseProperties) {
			TupleQueryResult result = vocabRepository.query(createSolitaryTransitiveRelationsQuery(transitivePropertyPair));
			addToResults(result);			
		}
		
		return new CollectionResult<Pair<URI>>(solitaryTransitiveRelations);
	}

	private String createSolitaryTransitiveRelationsQuery(
		String[] transitiveNontransiviteInverseProperties) 
	{
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource1 ?resource2 "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+

				"WHERE {" +
					"?resource1 " +transitiveNontransiviteInverseProperties[0]+ " ?resource2 . "+
					"FILTER NOT EXISTS {?resource1 ("+transitiveNontransiviteInverseProperties[1]+ "|^"+transitiveNontransiviteInverseProperties[2]+")* ?resource2}" +
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
