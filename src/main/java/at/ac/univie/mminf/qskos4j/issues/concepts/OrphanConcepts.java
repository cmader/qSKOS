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
import org.openrdf.query.TupleQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds all "orphan concepts". Further info on <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Orphan_Concepts">Orphan
 * Concepts</a>.
 */
public class OrphanConcepts extends Issue<Collection<Value>> {

    private InvolvedConcepts involvedConcepts;

    public OrphanConcepts(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts.getRepositoryConnection(),
              "oc",
              "Orphan Concepts",
              "Finds all orphan concepts, i.e. those not having semantic relationships to other concepts",
              IssueType.ANALYTICAL
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected Collection<Value> prepareData() throws OpenRDFException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createOrphanConceptsQuery());
        Set<Value> connectedConcepts = TupleQueryResultUtil.getValuesForBindingName(query.evaluate(), "concept");

        Set<Value> orphanConcepts = new HashSet<Value>(involvedConcepts.getPreparedData());
        orphanConcepts.removeAll(connectedConcepts);

        return orphanConcepts;
    }

    @Override
    protected Report prepareReport(Collection<Value> preparedData) {
        return new CollectionReport<Value>(preparedData);
    }

    private String createOrphanConceptsQuery() throws OpenRDFException
    {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?concept WHERE " +
            "{" +
                "{"+
                    "{?concept ?rel ?otherConcept} UNION " +
                    "{?otherConcept ?rel ?concept}" +
                    "?rel rdfs:subPropertyOf ?semRel . " +
                "}"+
                SkosOntology.getInstance().getSubPropertiesOfSemanticRelationsFilter("semRel")+
            "}";
    }

}
