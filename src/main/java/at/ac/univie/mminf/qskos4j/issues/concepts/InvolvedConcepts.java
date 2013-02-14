package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import info.aduna.webapp.system.SystemOverviewController;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import java.util.Iterator;
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
        String query = createConceptsQuery();
        System.out.println(query);
        TupleQueryResult result = vocabRepository.query(query);

        //Set<Value> foundConcepts = TupleQueryResultUtil.getValuesForBindingName(result, "concept");

        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Value concept = bindingSet.getValue("concept");
            Value p = bindingSet.getValue("p");
            Value semRelSubProp = bindingSet.getValue("semRelSubProp");

            System.out.println(concept.stringValue() +", p="+ p.stringValue() +", semRelSubProp="+ semRelSubProp.stringValue());
        }

        //return new CollectionReport<Value>(foundConcepts);
        return null;
    }

    private String createConceptsQuery() throws OpenRDFException {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?concept ?p ?semRelSubProp "+
                "WHERE {" +
/*
                    "{?concept rdf:type/rdfs:subClassOf* skos:Concept} UNION "+
                    "{?concept skos:topConceptOf ?conceptScheme} UNION "+
                    "{?conceptScheme skos:hasTopConcept ?concept} UNION " +
                    "{" +
                        "{?concept ?semRelSubProp ?x} UNION" +
                        "{?x ?semRelSubProp ?concept}" +
                        getFilterForSemanticRelations("semRelSubProp")+
                    "}" +
*/

//                    "{" +
                        "?concept ?p ?x . " +
                        "?p rdfs:subPropertyOf ?semRelSubProp . " +
//                        "{?x ?p ?concept . ?p rdfs:subPropertyOf+ ?semRelSubProp}" +
                         "FILTER (?semRelSubProp IN (<http://www.w3.org/2004/02/skos/core#broader>))"+
//                        getFilterForSemanticRelations("semRelSubProp")+
//                    "}"+

                /*
                    "{"+
                        "GRAPH <" +vocabRepository.SKOS_GRAPH_URL+ "> {"+
                            "?semRelSubProp rdfs:subPropertyOf+ skos:semanticRelation ."+
                        "}" +
                        "{" +
                            "{?x ?semRelSubProp ?concept . } UNION "+
                            "{?concept ?semRelSubProp ?x . } UNION " +
                            "{?concept ?p ?x . ?p rdfs:subPropertyOf+ ?semRelSubProp} UNION " +
                            "{?x ?p ?concept . ?p rdfs:subPropertyOf+ ?semRelSubProp}" +
                        "}"+
                    "}" +
                    */
                "}";

    }

    private String getFilterForSemanticRelations(String bindingName) throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createSubpropertiesOfSemanticRelationsQuery(), VocabRepository.RepositoryType.SKOS);
        Set<Value> semRelSubProperties = TupleQueryResultUtil.getValuesForBindingName(result, "semRelSubProp");

        String filterExpression = "FILTER (?" +bindingName+ " IN (";
        Iterator<Value> subPropIt = semRelSubProperties.iterator();
        while (subPropIt.hasNext()) {
            filterExpression += "<"+ subPropIt.next().stringValue() +">"+ (subPropIt.hasNext() ? "," : "))");
        }
        return filterExpression;
    }

    private String createSubpropertiesOfSemanticRelationsQuery() {
        return SparqlPrefix.SKOS +" "+  SparqlPrefix.RDFS +
            "SELECT ?semRelSubProp WHERE {" +
                "?semRelSubProp rdfs:subPropertyOf+ skos:semanticRelation" +
            "}";
    }

}
