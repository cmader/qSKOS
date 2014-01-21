package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

        Iterator<Resource> conceptIt = new MonitoredIterator<Resource>(authoritativeConcepts.getResult().getData(), progressMonitor);
        while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
            if (concept instanceof URI && isReflexivelyRelated((URI) concept)) {
                System.out.println(concept.stringValue());
            }
        }

        return results;
    }

    private boolean isReflexivelyRelated(URI resource) throws OpenRDFException {
        return repCon.prepareBooleanQuery(QueryLanguage.SPARQL,
                SparqlPrefix.SKOS +" "+  SparqlPrefix.RDFS+
                        "ASK {" +
                            "?resource ?relation ?resource . " +
                            "?relation rdfs:subPropertyOf skos:semanticRelation " +
                        "}").evaluate();

    }

    private boolean isAuthoritativeConcept(Resource resource) throws OpenRDFException {
        return authoritativeConcepts.getResult().getData().contains(resource);
    }

}
