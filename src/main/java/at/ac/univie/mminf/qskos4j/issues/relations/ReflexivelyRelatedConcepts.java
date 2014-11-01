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
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryResult;
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
    protected CollectionResult<Statement> invoke() throws OpenRDFException {
        return new CollectionResult<>(findReflexivelyRelatedResources());
    }

    private Collection<Statement> findReflexivelyRelatedResources() throws OpenRDFException
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
        catch (OpenRDFException e) {
            logger.error("Error finding relations of concept '" +resource+ "'");
        }
        return false;
    }

}
