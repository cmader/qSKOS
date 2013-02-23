package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.NumberReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:01
 *
 * Finds the number of triples involving (subproperties of) skos:semanticRelation.
 */
public class SemanticRelations extends Issue<NumberReport<Long>> {

    public SemanticRelations(RepositoryConnection repCon) {
        super(repCon,
              "sr",
              "Semantic Relations Count",
              "Counts the number of relations between concepts (skos:semanticRelation and subproperties thereof)",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberReport<Long> prepareData() throws OpenRDFException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createSemanticRelationsQuery());
        return new NumberReport<Long>(TupleQueryResultUtil.countResults(query.evaluate()));
    }

    private String createSemanticRelationsQuery() throws OpenRDFException
    {
        String skosSemanticRelationSubPropertiesFilter = SkosOntology.getInstance().getSubPropertiesOfSemanticRelationsFilter("relationType");
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT ?relationType WHERE " +
            "{" +
                "{?concept ?relationType ?otherConcept} UNION "+
                "{"+
                    "?x ?p ?y . " +
                    "?p rdfs:subPropertyOf ?relationType . " +
                "}"+
                skosSemanticRelationSubPropertiesFilter+
            "}";
    }

}
