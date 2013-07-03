package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.NumberReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:01
 *
 * Finds the number of triples involving (subproperties of) skos:semanticRelation.
 */
public class SemanticRelations extends Issue<Long> {

    public SemanticRelations() {
        super("sr",
              "Semantic Relations Count",
              "Counts the number of relations between concepts (skos:semanticRelation and subproperties thereof)",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected Long computeResult() throws OpenRDFException {
        RepositoryResult<Statement> result = repCon.getStatements(
            null,
            SkosOntology.getInstance().getUri("semanticRelation"),
            null,
            true);

        long semanticRelationsCount = 0;
        while (result.hasNext()) {
            result.next();
            semanticRelationsCount++;
        }
        return semanticRelationsCount;
    }

    @Override
    protected Report generateReport(Long preparedData) {
        return new NumberReport<Long>(preparedData);
    }

}
