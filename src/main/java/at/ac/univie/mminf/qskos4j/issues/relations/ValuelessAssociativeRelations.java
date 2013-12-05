package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.graph.AssociativeRelation;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;

import java.util.Collection;
import java.util.HashSet;

/**
* Finds all <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Valueless_Associative_Relations">Valueless Associative Relations</a>.
*/
public class ValuelessAssociativeRelations extends Issue<CollectionResult<ValuelessAssociativeRelations.ValuelessAssociativeRelation>> {

    public ValuelessAssociativeRelations() {
        super("var",
              "Valueless Associative Relations",
              "Finds sibling concept pairs that are also connected by an associative relation",
              IssueType.ANALYTICAL,
              new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#valueless-associative-relations"));
    }

    @Override
    protected CollectionResult<ValuelessAssociativeRelation> invoke() throws OpenRDFException {
		Collection<ValuelessAssociativeRelation> foundValuelessRelations = new HashSet<ValuelessAssociativeRelation>();

        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createRedundantAssociativeRelationsQuery());
        generateResultsList(foundValuelessRelations, query.evaluate());
		
		return new CollectionResult<ValuelessAssociativeRelation>(foundValuelessRelations);
	}

    private String createRedundantAssociativeRelationsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?parent ?child ?otherchild ?relation "+
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

				"?child ?relation ?otherchild. "+
                "FILTER (?relation IN (skos:related, skos:relatedMatch))"+
			"}";
	}
	
	private void generateResultsList(Collection<ValuelessAssociativeRelation> allResults, TupleQueryResult result)
		throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();

            Resource parent = (Resource) queryResult.getValue("parent");
            Resource child = (Resource) queryResult.getValue("child");
            Resource otherchild = (Resource) queryResult.getValue("otherchild");

            URI relation = (URI) queryResult.getValue("relation");

			allResults.add(new ValuelessAssociativeRelation(
                    new AssociativeRelation(child, otherchild, relation),
                    parent));
		}
	}

    public class ValuelessAssociativeRelation {

        private AssociativeRelation relatedConcepts;
        private Resource commonParent;

        ValuelessAssociativeRelation(AssociativeRelation relatedConcepts, Resource commonParent) {
            this.relatedConcepts = relatedConcepts;
            this.commonParent = commonParent;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ValuelessAssociativeRelation) {
                ValuelessAssociativeRelation other = (ValuelessAssociativeRelation) obj;
                return relatedConcepts.equals(other.relatedConcepts) && commonParent.equals(other.commonParent);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return relatedConcepts.hashCode() + commonParent.hashCode();
        }

        public AssociativeRelation getRelatedConcepts() {
            return relatedConcepts;
        }

        public Resource getCommonParent() {
            return commonParent;
        }

    }

}
