package at.ac.univie.mminf.qskos4j.issues.pp;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;

import java.util.Set;

public class NoBroaderAndNotTopConcepts extends Issue<CollectionReport<Value>> {

    private final static String QUERY =  SparqlPrefix.SKOS +" "+
        "SELECT DISTINCT ?concept WHERE {"+
            "?concept a skos:Concept . "+
            "OPTIONAL "+
            "{"+
                "?y skos:narrower|^skos:broader ?concept."+
            "}."+
            "OPTIONAL "+
            "{"+
                "?s skos:hasTopConcept ?concept. "+
            "}."+
            "FILTER ( !bound(?y) && !bound(?s) )"+
        "}";

    public NoBroaderAndNotTopConcepts(VocabRepository vocabRepository) {
        super(vocabRepository,
            "nbantc",
            "No Broader And No Top Concepts",
            "Finds concepts that don't have a broader concept defined and are not top concepts",
            IssueType.ANALYTICAL);
    }

    @Override
    protected CollectionReport<Value> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(QUERY);
        Set<Value> foundConcepts = TupleQueryResultUtil.getValuesForBindingName(result, "concept");

        return new CollectionReport<Value>(foundConcepts);
    }
}
