package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Finds top concepts that have broader concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Top_Concepts_Having_Broader_Concepts">Top Concepts Having Broader Concepts</a>
 */
public class TopConceptsHavingBroaderConcepts extends Issue<CollectionResult<Value> {

    protected TopConceptsHavingBroaderConcepts() {
        super("tchbc",
              "Top Concepts Having Broader Concepts",
              "Finds top concepts internal to the vocabulary hierarchy tree",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionResult<Value> invoke() throws OpenRDFException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
