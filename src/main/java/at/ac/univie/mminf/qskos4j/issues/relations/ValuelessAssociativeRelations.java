package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;

import java.util.Collection;
import java.util.HashSet;

/**
* Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
*/
public class ValuelessAssociativeRelations extends Issue<Collection<Pair<URI>>> {

    public ValuelessAssociativeRelations() {
        super("var",
              "Valueless Associative Relations",
              "Finds sibling concept pairs that are also connected by an associative relation",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#valueless-associative-relations"));
    }

    @Override
    protected Collection<Pair<URI>> computeResult() throws OpenRDFException {
		Collection<Pair<URI>> redundantAssociativeRelations = new HashSet<Pair<URI>>();

        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createRedundantAssociativeRelationsQuery());
        generateResultsList(redundantAssociativeRelations, query.evaluate());
		
		return redundantAssociativeRelations;
	}

    @Override
    protected Report generateReport(Collection<Pair<URI>> preparedData) {
        return new CollectionReport<Pair<URI>>(preparedData);
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
	
	private void generateResultsList(Collection<Pair<URI>> allResults, TupleQueryResult result)
		throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI child = (URI) queryResult.getValue("child");
			URI otherchild = (URI) queryResult.getValue("otherchild");

			allResults.add(new Pair<URI>(child, otherchild));
		}
	}

}
