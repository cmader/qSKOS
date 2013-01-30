package at.ac.univie.mminf.qskos4j.util;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;
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
                ret.add(concept);
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

    public static Collection<Pair<Value>> createCollectionOfValuePairs(TupleQueryResult result, String value1, String value2)
        throws OpenRDFException
    {
        Collection<Pair<Value>> resultCollection = new ArrayList<Pair<Value>>();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Value concept1 = queryResult.getValue(value1);
            Value concept2 = queryResult.getValue(value2);

            resultCollection.add(new Pair<Value>(concept1, concept2));
        }

        return resultCollection;
    }

}
