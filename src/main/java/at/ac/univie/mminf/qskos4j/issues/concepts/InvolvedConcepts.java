package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.Collection;

/**
 * Finds all <a href="http://www.w3.org/TR/skos-reference/#concepts">SKOS Concepts</a> involved in the vocabulary.
 */
public class InvolvedConcepts extends Issue<Collection<Value>> {

    public InvolvedConcepts() {
        super("c",
              "All Concepts",
              "Finds all SKOS concepts involved in the vocabulary",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected Collection<Value> prepareData() throws OpenRDFException
    {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createConceptsQuery()).evaluate();
        return TupleQueryResultUtil.getValuesForBindingName(result, "concept");
    }

    @Override
    protected Report prepareReport(Collection<Value> preparedData) {
        return new CollectionReport<Value>(preparedData);
    }

    private String createConceptsQuery() throws OpenRDFException {
        String skosSemanticRelationSubPropertiesFilter = SkosOntology.getInstance().getSubPropertiesOfSemanticRelationsFilter("semRelSubProp");

        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?concept "+
                "WHERE {" +
                    "{?concept rdf:type skos:Concept} UNION "+
                    "{?concept skos:topConceptOf ?conceptScheme} UNION "+
                    "{?conceptScheme skos:hasTopConcept ?concept} UNION " +
                    "{" +
                        "{?concept ?p ?x . } UNION" +
                        "{?x ?p ?concept . }" +
                        "?p rdfs:subPropertyOf ?semRelSubProp .  " +
                        skosSemanticRelationSubPropertiesFilter+
                    "}"+
                "}";
    }

}
