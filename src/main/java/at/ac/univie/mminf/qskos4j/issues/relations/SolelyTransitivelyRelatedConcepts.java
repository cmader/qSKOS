package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
        super(new IssueDescriptor.Builder("strc",
              "Solely Transitively Related Concepts",
              "Concepts only related by skos:broaderTransitive or skos:narrowerTransitive",
              IssueDescriptor.IssueType.ANALYTICAL)
                .weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#solely-transitively-related-concepts")
                .build()
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

        return new CollectionResult<>(relatedConcepts);
    }


    private String createSolitaryTransitiveRelationsQuery(
		String[] transitiveNontransitiveInverseProperties)
	{
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource1 ?resource2 "+
				"WHERE {" +
					"?resource1 " +transitiveNontransitiveInverseProperties[0]+ " ?resource2 . "+
					"FILTER NOT EXISTS {?resource1 ("+transitiveNontransitiveInverseProperties[1]+ "|^"+transitiveNontransitiveInverseProperties[2]+")* ?resource2}" +
					"}";
	}

    private void addToResults(TupleQueryResult result, String solitaryRelation)
            throws QueryEvaluationException
    {
        ValueFactory factory = SimpleValueFactory.getInstance();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Resource resource1 = (Resource) queryResult.getValue("resource1");
            Value resource2 = queryResult.getValue("resource2");
            IRI relation = factory.createIRI(solitaryRelation.replace("skos:", SparqlPrefix.SKOS.getNameSpace()));

            solitaryTransitiveRelations.add(factory.createStatement(resource1, relation, resource2));
        }
    }

}
