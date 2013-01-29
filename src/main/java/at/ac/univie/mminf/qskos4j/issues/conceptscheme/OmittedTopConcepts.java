package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

/**
 * Finds concept schemes without top concepts (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Omitted_Top_Concepts">Omitted Top Concepts</a>
 * ).
 */
public class OmittedTopConcepts extends Issue<CollectionResult<Resource> {

    protected OmittedTopConcepts() {
        super("otc",
              "Omitted Top Concepts",
              "Finds skos:ConceptSchemes without top concepts",
              IssueType.ANALYTICAL
        );
    }

    @Override
    protected CollectionResult<Resource> invoke() throws OpenRDFException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
