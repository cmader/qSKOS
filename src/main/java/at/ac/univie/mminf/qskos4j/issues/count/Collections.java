package at.ac.univie.mminf.qskos4j.issues.count;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.NumberResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryResult;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:23
 *
 * Finds the number of SKOS <a href="http://www.w3.org/TR/skos-reference/#collections">Collections</a>.
 */
public class Collections extends Issue<NumberResult<Long>> {

    public Collections() {
        super("cc",
              "Collection Count",
              "Counts the involved Collections",
              IssueType.STATISTICAL
        );
    }

    @Override
    protected NumberResult<Long> invoke() throws OpenRDFException {
        RepositoryResult<Statement> result = repCon.getStatements(null, RDF.TYPE, SkosOntology.getInstance().getUri("Collection"), true);

        long collectionCount = 0;
        while (result.hasNext()) {
            result.next();
            collectionCount++;
        }
        result.close();

        return new NumberResult<Long>(collectionCount);
    }

}
