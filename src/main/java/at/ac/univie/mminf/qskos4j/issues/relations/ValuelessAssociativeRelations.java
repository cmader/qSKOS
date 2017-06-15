package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.*;

import java.util.Collection;
import java.util.HashSet;

/**
 * Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
 */
public class ValuelessAssociativeRelations extends Issue<CollectionResult<Tuple<URI>>> {

    public ValuelessAssociativeRelations() {
        super("var",
                "Valueless Associative Relations",
                "Finds sibling concept pairs that are also connected by an associative relation",
                IssueType.ANALYTICAL,
                new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#valueless-associative-relations"));
    }

    @Override
    protected CollectionResult<Tuple<URI>> invoke() throws RDF4JException {
        Collection<Tuple<URI>> redundantAssociativeRelations = new HashSet<Tuple<URI>>();

        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createRedundantAssociativeRelationsQuery());
        generateResultsList(redundantAssociativeRelations, query.evaluate());

        return new CollectionResult<Tuple<URI>>(redundantAssociativeRelations);
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

    private void generateResultsList(Collection<Tuple<URI>> allResults, TupleQueryResult result)
            throws QueryEvaluationException
    {
        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            URI child = (URI) queryResult.getValue("child");
            URI otherchild = (URI) queryResult.getValue("otherchild");

            allResults.add(new Tuple<URI>(child, otherchild));
        }
    }

}
