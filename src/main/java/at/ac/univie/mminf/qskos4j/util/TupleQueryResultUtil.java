package at.ac.univie.mminf.qskos4j.util;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.HashSet;
import java.util.Set;

public class TupleQueryResultUtil {

    public static Set<Value> getValuesForBindingName(TupleQueryResult result, String bindingName)
        throws QueryEvaluationException
    {
        Set<Value> ret = new HashSet<Value>();

        while (result.hasNext()) {
            Value concept = result.next().getValue(bindingName);

            if (concept instanceof URI) {
                ret.add((URI) concept);
            }
        }

        return ret;
    }

    public static long countResults(TupleQueryResult result) throws QueryEvaluationException
    {
        long count = 0;

        while (result.hasNext()) {
            count++;
            result.next();
        }

        return count;
    }

}
