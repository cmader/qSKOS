package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.Map;

public class OmittedOrInvalidLanguageTagsResult extends Result<Map<Resource, Collection<String>>> {

    OmittedOrInvalidLanguageTagsResult(Map<Resource, Collection<String>> data) {
        super(data);
    }

    @Override
    public boolean indicatesProblem() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
