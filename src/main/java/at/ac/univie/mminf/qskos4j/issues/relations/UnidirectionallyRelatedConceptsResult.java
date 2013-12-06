package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import org.openrdf.model.Resource;

import java.util.Map;

public class UnidirectionallyRelatedConceptsResult extends Result<Map<Tuple<Resource>, String>> {

    protected UnidirectionallyRelatedConceptsResult(Map<Tuple<Resource>, String> data) {
        super(data);
    }

    @Override
    public boolean indicatesProblem() {
        return !getData().isEmpty();
    }
}
