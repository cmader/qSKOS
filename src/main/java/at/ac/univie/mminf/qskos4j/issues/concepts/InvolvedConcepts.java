package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Finds all <a href="http://www.w3.org/TR/skos-reference/#concepts">SKOS Concepts</a> involved in the vocabulary.
 */
public class InvolvedConcepts extends Issue<CollectionResult<Resource>> {

    public InvolvedConcepts() {
        super("c",
              "All Concepts",
              "Finds all SKOS concepts involved in the vocabulary",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
        RepositoryResult<Statement> result = repCon.getStatements(
            null,
            RDF.TYPE,
            SkosOntology.getInstance().getUri("Concept"),
            true);

        Collection<Resource> involvedConcepts = new ArrayList<Resource>();
        while (result.hasNext()) {
            involvedConcepts.add(result.next().getSubject());
        }

        return new CollectionResult<Resource>(involvedConcepts);
    }

}
