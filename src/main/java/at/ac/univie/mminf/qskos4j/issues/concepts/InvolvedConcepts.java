package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;

import java.util.Set;

/**
 * Finds all <a href="http://www.w3.org/TR/skos-reference/#concepts">SKOS Concepts</a> involved in the vocabulary.
 */
public class InvolvedConcepts extends Issue<CollectionReport<Value>> {

    public InvolvedConcepts(VocabRepository vocabRepo) {
        super(vocabRepo,
              "c",
              "All Concepts",
              "Finds all SKOS concepts involved in the vocabulary",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected CollectionReport<Value> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createConceptsQuery());
        Set<Value> foundConcepts = TupleQueryResultUtil.getValuesForBindingName(result, "concept");
        return new CollectionReport<Value>(foundConcepts);
    }

    private String createConceptsQuery() throws OpenRDFException {
        String skosSemanticRelationSubPropertiesFilter = createFilterForSemanticRelations();

        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?concept "+
                "WHERE {" +

                    "{?concept rdf:type/rdfs:subClassOf* skos:Concept} UNION "+
                    "{?concept skos:topConceptOf ?conceptScheme} UNION "+
                    "{?conceptScheme skos:hasTopConcept ?concept} UNION " +
                    "{" +
                        "{?concept ?semRelSubProp ?x} UNION" +
                        "{?x ?semRelSubProp ?concept}" +
                        skosSemanticRelationSubPropertiesFilter+
                    "} UNION " +
                    "{" +
                        "{?concept ?p ?x . } UNION" +
                        "{?x ?p ?concept . }" +
                        "?p rdfs:subPropertyOf ?semRelSubProp .  " +
                        skosSemanticRelationSubPropertiesFilter+
                    "}"+
                "}";
    }

    private String createFilterForSemanticRelations() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createSubpropertiesOfSemanticRelationsQuery(), VocabRepository.RepositoryType.SKOS);
        return TupleQueryResultUtil.getFilterForBindingName(result, "semRelSubProp");
    }

    private String createSubpropertiesOfSemanticRelationsQuery() {
        return SparqlPrefix.SKOS +" "+  SparqlPrefix.RDFS +
            "SELECT ?semRelSubProp WHERE {" +
                "?semRelSubProp rdfs:subPropertyOf+ skos:semanticRelation" +
            "}";
    }

}
