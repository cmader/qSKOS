package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.AdHocCheckable;
import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import java.util.Arrays;
import java.util.Iterator;

public class HierarchicalCyclesAdHoc implements AdHocCheckable {

    private enum HierarchyType {BROADER, NARROWER, NONE}

    private Repository repository;

    public HierarchicalCyclesAdHoc(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException
    {
        HierarchyType hierarchyType = getPredicateHierarchyType(statement.getPredicate());
        if (hierarchyType == HierarchyType.NONE) return;

        RepositoryConnection repCon = repository.getConnection();
        try {
            String query = createHierarchicalCycleQuery(statement.getSubject(), statement.getObject(), hierarchyType);
            BooleanQuery hierarchicalQuery = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, query);
            if (hierarchicalQuery.evaluate()) {
                throw new IssueOccursException();
            }
        }
        finally {
            repCon.close();
        }
    }

    private String createHierarchicalCycleQuery(Resource subject, Value object, HierarchyType hierarchyType)
    {
        return SparqlPrefix.SKOS +" "+
                "ASK {<" +object+ "> (" +getHierarchicalProperties(hierarchyType)+ ")* <" +subject+ ">}";
    }

    private String getHierarchicalProperties(HierarchyType hierarchyType) {
        Iterator<URI> broaderIt = Arrays.asList(SkosOntology.SKOS_BROADER_PROPERTIES).iterator();
        Iterator<URI> narrowerIt = Arrays.asList(SkosOntology.SKOS_NARROWER_PROPERTIES).iterator();

        switch (hierarchyType) {
            case BROADER:
                return concatWithOrOperator(broaderIt, false) +"|"+ concatWithOrOperator(narrowerIt, true);

            case NARROWER:
                return concatWithOrOperator(narrowerIt, false) +"|"+ concatWithOrOperator(broaderIt, true);

            default:
                return "";
        }
    }

    private String concatWithOrOperator(Iterator<URI> iterator, boolean addInversePrefix) {
        String concatedEntries = "";
        while (iterator.hasNext()) {
            concatedEntries += (addInversePrefix ? "^" : "") +"<"+ iterator.next() +">"+ (iterator.hasNext() ? "|" : "");
        }
        return concatedEntries;
    }

    private HierarchyType getPredicateHierarchyType(URI predicate) {
        if (Arrays.asList(SkosOntology.SKOS_BROADER_PROPERTIES).contains(predicate)) return HierarchyType.BROADER;
        if (Arrays.asList(SkosOntology.SKOS_NARROWER_PROPERTIES).contains(predicate)) return HierarchyType.NARROWER;

        return HierarchyType.NONE;
    }
}
