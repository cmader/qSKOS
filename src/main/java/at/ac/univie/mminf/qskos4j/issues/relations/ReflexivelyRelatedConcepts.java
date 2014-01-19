package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;

public class ReflexivelyRelatedConcepts extends Issue<CollectionResult<Statement>> {

    private AuthoritativeConcepts authoritativeConcepts;

    public ReflexivelyRelatedConcepts(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
              "rrc",
              "Reflexively Related Concepts",
              "Finds concepts that are related to themselves",
                IssueType.ANALYTICAL,
                new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Reflexive_Relations"));
        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected CollectionResult<Statement> invoke() throws OpenRDFException {
        return new CollectionResult<>(findReflexivelyRelatedResources());
    }

    private Collection<Statement> findReflexivelyRelatedResources() throws OpenRDFException
    {
        Collection<Statement> results = new ArrayList<>();

        TupleQuery query = repCon.prepareTupleQuery(QueryLanguage.SPARQL,
                                                    "SELECT ?resource ?relation WHERE {" +
                                                            "?resourceA ?relation ?resourceB . " +
                                                            "FILTER (?resourceA = ?resourceB)" +
                                                            "}");
        TupleQueryResult result = query.evaluate();
        while (result.hasNext()) {
            Resource resource = (Resource) result.next().getValue("resource");
            URI relation = (URI) result.next().getValue("relation");

            if (isAuthoritativeConcept(resource)) {
                results.add(new StatementImpl(resource, relation, resource));
            }
        }

        return results;
    }

    private boolean isAuthoritativeConcept(Resource resource) throws OpenRDFException {
        return authoritativeConcepts.getResult().getData().contains(resource);
    }

}
