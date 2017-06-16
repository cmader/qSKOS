package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.StatementImpl;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Finds <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Solely_Transitively_Related_Concepts">Solely Transitively Related Concepts</a>.
 */
public class SolelyTransitivelyRelatedConcepts extends Issue<CollectionResult<Tuple<Resource>>> {

	private String[][] transitiveNontransiviteInverseProperties = {
			{"skos:broaderTransitive", "skos:broader", "skos:narrower"},
			{"skos:narrowerTransitive", "skos:narrower", "skos:broader"}};

    private Collection<Statement> solitaryTransitiveRelations;

    public SolelyTransitivelyRelatedConcepts() {
        super("strc",
              "Solely Transitively Related Concepts",
              "Concepts only related by skos:broaderTransitive or skos:narrowerTransitive",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#solely-transitively-related-concepts")
        );
    }

    @Override
    protected CollectionResult<Tuple<Resource>> invoke() throws RDF4JException {
        solitaryTransitiveRelations = new ArrayList<>();
        for (String[] transitivePropertyPair : transitiveNontransiviteInverseProperties) {
            String query = createSolitaryTransitiveRelationsQuery(transitivePropertyPair);
            TupleQuery tupleQuery = repCon.prepareTupleQuery(QueryLanguage.SPARQL, query);
            addToResults(tupleQuery.evaluate(), transitivePropertyPair[0]);
        }

        return createReport();
	}

    private CollectionResult<Tuple<Resource>> createReport() {
        Collection<Tuple<Resource>> relatedConcepts = new HashSet<>();
        for (Statement statement : solitaryTransitiveRelations) {
            relatedConcepts.add(new Tuple<>(statement.getSubject(), (Resource) statement.getObject()));
        }

        return new CollectionResult(relatedConcepts);
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

    private void addToResults(TupleQueryResult result, String solitaryRelation)
            throws QueryEvaluationException
    {
        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Resource resource1 = (Resource) queryResult.getValue("resource1");
            Value resource2 = queryResult.getValue("resource2");
            URI relation = new URIImpl(solitaryRelation.replace("skos:", SparqlPrefix.SKOS.getNameSpace()));

            solitaryTransitiveRelations.add(new StatementImpl(resource1, relation, resource2));
        }
    }

}
