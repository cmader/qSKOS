package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryResult;

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
    protected CollectionResult<Resource> invoke() throws OpenRDFException {
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
