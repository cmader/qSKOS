package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.query.*;

import java.util.Collection;
import java.util.HashSet;

/**
* Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
*/
public class ValuelessAssociativeRelations extends Issue<Collection<Tuple<Resource>>> {

    public ValuelessAssociativeRelations() {
        super("var",
              "Valueless Associative Relations",
              "Two concepts are sibling, but also connected by an associative relation",
              IssueType.ANALYTICAL);
    }

    @Override
    protected Collection<Tuple<Resource>> computeResult() throws OpenRDFException {
		Collection<Tuple<Resource>> redundantAssociativeRelations = new HashSet<Tuple<Resource>>();

        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createRedundantAssociativeRelationsQuery());
        generateResultsList(redundantAssociativeRelations, query.evaluate());
		
		return redundantAssociativeRelations;
	}

    @Override
    protected Report generateReport(Collection<Tuple<Resource>> preparedData) {
        return new CollectionReport<Tuple<Resource>>(preparedData);
    }

    private String createRedundantAssociativeRelationsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?parent ?child ?otherchild "+
			"WHERE {" +
				"{" +
					"?parent skos:narrower|skos:narrowerTransitive|^skos:broader|^skos:broaderTransitive ?child . " +
					"?parent skos:narrower|skos:narrowerTransitive|^skos:broader|^skos:broaderTransitive ?otherchild . " +
				"}" +
				"UNION" +
				"{" +
					"?child skos:narrower|skos:narrowerTransitive|^skos:broader|^skos:broaderTransitive ?parent . " +
					"?otherchild skos:narrower|skos:narrowerTransitive|^skos:broader|^skos:broaderTransitive ?parent . " +
				"}" +

				"?child skos:related|skos:relatedMatch ?otherchild. "+
			"}";
	}
	
	private void generateResultsList(Collection<Tuple<Resource>> allResults, TupleQueryResult result)
		throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
            Resource child = (Resource) queryResult.getValue("child");
            Resource otherchild = (Resource) queryResult.getValue("otherchild");
            Resource parent = (Resource) queryResult.getValue("parent");

			allResults.add(new Tuple<Resource>(child, otherchild, parent));
		}
	}

}
