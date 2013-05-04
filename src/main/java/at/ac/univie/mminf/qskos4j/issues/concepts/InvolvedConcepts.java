package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Finds all <a href="http://www.w3.org/TR/skos-reference/#concepts">SKOS Concepts</a> involved in the vocabulary.
 */
public class InvolvedConcepts extends Issue<Collection<URI>> {

    public InvolvedConcepts() {
        super("c",
              "All Concepts",
              "Finds all SKOS concepts involved in the vocabulary",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected Collection<URI> computeResult() throws OpenRDFException
    {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createConceptsQuery()).evaluate();

        Collection<URI> involvedConcepts = new ArrayList<URI>();
        while (result.hasNext()) {
            Value concept = result.next().getBinding("concept").getValue();
            if (concept instanceof URI) involvedConcepts.add((URI) concept);
        }
        return involvedConcepts;
    }

    @Override
    protected Report generateReport(Collection<URI> preparedData) {
        return new CollectionReport<URI>(preparedData);
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
