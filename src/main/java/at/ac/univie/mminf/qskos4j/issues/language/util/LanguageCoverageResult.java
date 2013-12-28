package at.ac.univie.mminf.qskos4j.issues.language.util;

import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.Map;

public class LanguageCoverageResult extends Result<Map<Resource, Collection<String>>> {

    protected LanguageCoverageResult(Map<Resource, Collection<String>> data) {
        super(data);
    }

    @Override
    public long occurrenceCount() {
        return getData().size();
    }

}
