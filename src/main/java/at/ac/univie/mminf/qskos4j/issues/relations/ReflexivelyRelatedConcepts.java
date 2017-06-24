package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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
        super(new IssueDescriptor.Builder(
              "rrc",
              "Reflexively Related Concepts",
              "Finds concepts that are related to themselves",
                IssueDescriptor.IssueType.ANALYTICAL)
                .weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Reflexive_Relations")
                .dependentIssue(authoritativeConcepts)
                .build());

        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected CollectionResult<Statement> invoke() throws RDF4JException {
        return new CollectionResult<>(findReflexivelyRelatedResources());
    }

    private Collection<Statement> findReflexivelyRelatedResources() throws RDF4JException
    {
        Collection<Statement> results = new ArrayList<>();
        ValueFactory factory = SimpleValueFactory.getInstance();

        Iterator<Resource> conceptIt = new MonitoredIterator<>(authoritativeConcepts.getResult().getData(), progressMonitor);
        while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
            if (concept instanceof IRI && isReflexivelyRelated((IRI) concept)) {
                RepositoryResult<Statement> reflexiveProperties = repCon.getStatements(concept, null, concept, false);

                while (reflexiveProperties.hasNext()) {
                    results.add(factory.createStatement(concept, reflexiveProperties.next().getPredicate(), concept));
                }
            }
        }

        return results;
    }

    private boolean isReflexivelyRelated(IRI resource) {
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

}
