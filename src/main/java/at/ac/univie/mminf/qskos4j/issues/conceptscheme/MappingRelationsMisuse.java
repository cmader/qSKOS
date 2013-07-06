package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;

public class MappingRelationsMisuse extends Issue<Collection<Statement>> {

    public MappingRelationsMisuse(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
            "mri",
            "Mapping Relations Misuse",
            "Finds concepts within the same concept scheme that are related by a mapping relation",
            IssueType.ANALYTICAL);
    }

    @Override
    protected Collection<Statement> computeResult() throws OpenRDFException {
        Collection<Statement> problematicRelations = new ArrayList<Statement>();

        TupleQueryResult result = repCon.prepareTupleQuery(QueryLanguage.SPARQL, createQuery()).evaluate();
        while (result.hasNext()) {
            BindingSet bs = result.next();
            Resource concept = (Resource) bs.getValue("concept");
            Resource otherConcept = (Resource) bs.getValue("otherConcept");
            URI relation = (URI) bs.getValue("otherConcept");

            problematicRelations.add(new StatementImpl(concept, relation, otherConcept));
        }

        return problematicRelations;
    }

    private String createQuery() {
        return SparqlPrefix.SKOS +
            "SELECT * WHERE {" +
                "?concept ?mappingRelation ?otherConcept . " +
                "?concept skos:inScheme ?conceptScheme ." +
                "?otherConcept skos:inScheme ?conceptScheme ." +
                "FILTER (?mappingRelation IN (skos:mappingRelation))" +
            "}";
    }

    @Override
    protected Report generateReport(Collection<Statement> preparedData) {
        return new CollectionReport<Statement>(preparedData);
    }
}
