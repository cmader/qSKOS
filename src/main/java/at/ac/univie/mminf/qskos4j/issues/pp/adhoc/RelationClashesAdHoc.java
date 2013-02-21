package at.ac.univie.mminf.qskos4j.issues.pp.adhoc;

import at.ac.univie.mminf.qskos4j.issues.pp.adhoc.AdHocCheckable;
import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class RelationClashesAdHoc implements AdHocCheckable {

    private Repository repository;

    private enum RelationType {HIERARCHICAL, ASSOCIATIVE, OTHER}

    public RelationClashesAdHoc(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException
    {
        boolean clashFound = false;

        if (getRelationType(statement.getPredicate()) == RelationType.HIERARCHICAL) {
            clashFound = pathExists(statement.getSubject(), statement.getObject(), RelationType.ASSOCIATIVE);
        }
        else if (getRelationType(statement.getPredicate()) == RelationType.ASSOCIATIVE) {
            clashFound = pathExists(statement.getSubject(), statement.getObject(), RelationType.HIERARCHICAL);
        }

        if (clashFound) throw new IssueOccursException();
    }

    private RelationType getRelationType(URI predicate) {
        if (Arrays.asList(SkosOntology.SKOS_BROADER_PROPERTIES).contains(predicate) ||
            Arrays.asList(SkosOntology.SKOS_NARROWER_PROPERTIES).contains(predicate))
        {
            return RelationType.HIERARCHICAL;
        }

        if (Arrays.asList(SkosOntology.SKOS_ASSOCIATIVE_PROPERTIES).contains(predicate)) {
            return RelationType.ASSOCIATIVE;
        }

        return RelationType.OTHER;
    }

    private boolean pathExists(Resource subject, Value object, RelationType relationType)
        throws RepositoryException
    {
        RepositoryConnection repCon = repository.getConnection();
        try {
            String query = SparqlPrefix.SKOS +" ASK {<" +subject+ ">" +createPropertyPath(relationType)+ "<" +object+ ">}";
            repCon.prepareBooleanQuery(QueryLanguage.SPARQL, )

        }
        finally {
            repCon.close();
        }
    }

}
