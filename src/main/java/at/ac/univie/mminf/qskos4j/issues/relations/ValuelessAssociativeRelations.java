package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;
import java.util.HashSet;

/**
* Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
*/
public class ValuelessAssociativeRelations extends Issue<CollectionReport<Pair<URI>>> {

    public ValuelessAssociativeRelations(VocabRepository vocabRepo) {
        super(vocabRepo,
              "var",
              "Valueless Associative Relations",
              "Two concepts are sibling, but also connected by an associative relation",
              IssueType.ANALYTICAL);
    }

    @Override
    protected CollectionReport<Pair<URI>> invoke() throws OpenRDFException {
		Collection<Pair<URI>> redundantAssociativeRelations = new HashSet<Pair<URI>>();
		
		TupleQueryResult result = vocabRepository.query(createRedundantAssociativeRelationsQuery());
		generateResultsList(redundantAssociativeRelations, result);
		
		return new CollectionReport<Pair<URI>>(redundantAssociativeRelations);
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
