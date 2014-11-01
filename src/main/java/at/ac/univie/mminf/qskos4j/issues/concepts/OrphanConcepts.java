package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;

import java.util.HashSet;
import java.util.Set;

/**
 * Finds all "orphan concepts". Further info on <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Orphan_Concepts">Orphan
 * Concepts</a>.
 */
public class OrphanConcepts extends Issue<CollectionResult<Resource>> {

    private InvolvedConcepts involvedConcepts;

    public OrphanConcepts(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts,
            "oc",
            "Orphan Concepts",
            "Finds all orphan concepts, i.e. those not having semantic relationships to other concepts",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#orphan-concepts")
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected CollectionResult<Resource> invoke() throws OpenRDFException {
        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createOrphanConceptsQuery());
        Set<Value> connectedConcepts = TupleQueryResultUtil.getValuesForBindingName(query.evaluate(), "concept");

        Set<Resource> orphanConcepts = new HashSet<>(involvedConcepts.getResult().getData());
        orphanConcepts.removeAll(connectedConcepts);

        return new CollectionResult<>(orphanConcepts);
    }

    private String createOrphanConceptsQuery() {
        return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
            "SELECT DISTINCT ?concept WHERE " +
            "{" +
                "{?concept ?rel ?otherConcept} UNION " +
                "{?otherConcept ?rel ?concept}" +
                "?rel rdfs:subPropertyOf skos:semanticRelation" +
            "}";
    }

}
