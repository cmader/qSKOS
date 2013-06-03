package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Solely_Transitively_Related_Concepts">Solely Transitively Related Concepts</a>.
 */
public class SolelyTransitivelyRelatedConcepts extends Issue<Collection<Tuple<URI>>> {

	private String[][] transitiveNontransiviteInverseProperties = {
			{"skos:broaderTransitive", "skos:broader", "skos:narrower"},
			{"skos:narrowerTransitive", "skos:narrower", "skos:broader"}};
	
	private Set<Tuple<URI>> solitaryTransitiveRelations = new HashSet<Tuple<URI>>();

    public SolelyTransitivelyRelatedConcepts() {
        super("strc",
              "Solely Transitively Related Concepts",
              "Concepts only related by skos:broaderTransitive or skos:narrowerTransitive",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected Collection<Tuple<URI>> computeResult() throws OpenRDFException {
		for (String[] transitivePropertyPair : transitiveNontransiviteInverseProperties) {
            TupleQuery query = repCon.prepareTupleQuery(
                QueryLanguage.SPARQL,
                createSolitaryTransitiveRelationsQuery(transitivePropertyPair));
			addToResults(query.evaluate());
		}
		
		return solitaryTransitiveRelations;
	}

    @Override
    protected Report generateReport(Collection<Tuple<URI>> preparedData) {
        return new CollectionReport<Tuple<URI>>(preparedData);
    }

    private String createSolitaryTransitiveRelationsQuery(
		String[] transitiveNontransiviteInverseProperties) 
	{
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource1 ?resource2 "+
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
			
			solitaryTransitiveRelations.add(new Tuple<URI>(resource1, resource2));
		}
	}

}
