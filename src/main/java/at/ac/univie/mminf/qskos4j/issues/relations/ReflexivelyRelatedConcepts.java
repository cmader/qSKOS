package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.impl.StatementImpl;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ReflexivelyRelatedConcepts extends Issue<CollectionResult<Statement>> {

    private final Logger logger = LoggerFactory.getLogger(ReflexivelyRelatedConcepts.class);

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
    protected CollectionResult<Statement> invoke() throws RDF4JException {
        return new CollectionResult<>(findReflexivelyRelatedResources());
    }

    private Collection<Statement> findReflexivelyRelatedResources() throws RDF4JException
    {
        Collection<Statement> results = new ArrayList<>();

        Iterator<Resource> conceptIt = new MonitoredIterator<>(authoritativeConcepts.getResult().getData(), progressMonitor);
        while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
            if (concept instanceof URI && isReflexivelyRelated((URI) concept)) {
                RepositoryResult<Statement> reflexiveProperties = repCon.getStatements(concept, null, concept, false);

                while (reflexiveProperties.hasNext()) {
                    results.add(new StatementImpl(concept, reflexiveProperties.next().getPredicate(), concept));
                }
            }
        }

        return results;
    }

    private boolean isReflexivelyRelated(URI resource) {
        try {
            return repCon.prepareBooleanQuery(QueryLanguage.SPARQL,
                SparqlPrefix.SKOS + " " + SparqlPrefix.RDFS +
                "ASK {" +
                    "<" + resource.stringValue() + "> ?relation <" + resource.stringValue() + "> . " +
                    "?relation rdfs:subPropertyOf skos:semanticRelation " +
                "}").evaluate();
        }
        catch (RDF4JException e) {
            logger.error("Error finding relations of concept '" +resource+ "'");
        }
        return false;
    }

    private boolean isAuthoritativeConcept(Resource resource) throws RDF4JException {
        return authoritativeConcepts.getResult().getData().contains(resource);
    }

}
