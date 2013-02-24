package at.ac.univie.mminf.qskos4j.issues.pp;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;

import java.util.Collection;

public class LabelMatch extends Issue<Collection<Value>> {

    private String regex;

    public LabelMatch(RepositoryConnection repCon, String regex) {
        super(repCon,
            "lm",
            "Label Match",
            "Finds concepts with labels that match a given regular expression",
            IssueType.ANALYTICAL);

        this.regex = regex;
    }

    @Override
    protected Collection<Value> prepareData() throws OpenRDFException {
        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, generateQuery()).evaluate();
        return TupleQueryResultUtil.getValuesForBindingName(result, "concept");
    }

    @Override
    protected Report prepareReport(Collection<Value> preparedData) {
        return new CollectionReport<Value>(preparedData);
    }

    private String generateQuery() {
        return SparqlPrefix.SKOS +
            "SELECT ?concept ?label WHERE {"+
                "?concept skos:prefLabel|skos:altLabel|skos:hiddenLabel ?label ."+
                "FILTER (regex(?label, '" +regex+ "'))"+
            "}";
    }
}
