package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.TupleQueryResult;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds all "orphan concepts". Further info on <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Orphan_Concepts">Orphan
 * Concepts</a>.
 */
public class OrphanConcepts extends Issue<CollectionReport<Value>> {

    private InvolvedConcepts involvedConcepts;

    public OrphanConcepts(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts.getVocabRepository(),
              "oc",
              "Orphan Concepts",
              "Finds all orphan concepts, i.e. those not having semantic relationships to other concepts",
              IssueType.ANALYTICAL
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected CollectionReport<Value> invoke() throws OpenRDFException {
        TupleQueryResult result = vocabRepository.query(createOrphanConceptsQuery());
        Set<Value> connectedConcepts = TupleQueryResultUtil.getValuesForBindingName(result, "concept");

        Set<Value> orphanConcepts = new HashSet<Value>(involvedConcepts.getReport().getData());
        orphanConcepts.removeAll(connectedConcepts);

        return new CollectionReport<Value>(orphanConcepts);
    }

    private String createOrphanConceptsQuery() throws OpenRDFException
    {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?concept WHERE " +
            "{" +
                "{" +
                    "{?concept ?semRel ?otherConcept} UNION " +
                    "{?otherConcept ?semRel ?concept}" +
                "} UNION " +
                "{"+
                    "{?concept ?rel ?otherConcept} UNION " +
                    "{?otherConcept ?rel ?concept}" +
                    "?rel rdfs:subPropertyOf ?semRel . " +
                "}"+
                SkosOntology.getInstance().getSubPropertiesOfSemanticRelationsFilter("semRel")+
            "}";
    }

}
