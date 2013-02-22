package at.ac.univie.mminf.qskos4j.issues.pp.adhoc;

import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology.HierarchyType;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;

public class HierarchicalCyclesAdHoc implements AdHocCheckable {

    private RepositoryConnection repCon;

    public HierarchicalCyclesAdHoc(RepositoryConnection repCon) {
        this.repCon = repCon;
    }

    @Override
    public void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException
    {
        try {
            HierarchyType hierarchyType = SkosOntology.getInstance().getPredicateHierarchyType(statement.getPredicate());
            String query = createHierarchicalCycleQuery(statement.getSubject(), statement.getObject(), hierarchyType);
            BooleanQuery hierarchicalQuery = repCon.prepareBooleanQuery(QueryLanguage.SPARQL, query);
            if (hierarchicalQuery.evaluate()) {
                throw new IssueOccursException();
            }
        }
        catch (IllegalArgumentException e) {
            // not a hierarchical property, do nothing
        }
    }

    private String createHierarchicalCycleQuery(Resource subject, Value object, HierarchyType hierarchyType)
    {
        return SparqlPrefix.SKOS +" "+
                "ASK {<" +object+ "> (" +SkosOntology.getInstance().getHierarchicalPropertiesPath(hierarchyType)+ ")* <" +subject+ ">}";
    }

}
