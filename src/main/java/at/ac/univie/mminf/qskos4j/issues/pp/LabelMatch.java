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

public class LabelMatch extends Issue<CollectionReport<Value>> {

    private String regex;

    public LabelMatch(VocabRepository vocabRepository, String regex) {
        super(vocabRepository,
            "lm",
            "Label Match",
            "Finds concepts with labels that match a given regular expression",
            IssueType.ANALYTICAL);

        this.regex = regex;
    }

    @Override
    protected CollectionReport<Value> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(generateQuery());
        Set<Value> foundConcepts = TupleQueryResultUtil.getValuesForBindingName(result, "concept");
        return new CollectionReport<Value>(foundConcepts);
    }

    private String generateQuery() {
        return SparqlPrefix.SKOS +
            "SELECT ?concept ?label WHERE {"+
                "?concept skos:prefLabel|skos:altLabel|skos:hiddenLabel ?label ."+
                "FILTER (regex(?label, '" +regex+ "'))"+
            "}";
    }
}
