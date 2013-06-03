package at.ac.univie.mminf.qskos4j.util;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.*;

public class TupleQueryResultUtil {

    public static Set<Value> getValuesForBindingName(TupleQueryResult result, String bindingName)
        throws QueryEvaluationException
    {
        Set<Value> ret = new HashSet<Value>();

        while (result.hasNext()) {
            ret.add(result.next().getValue(bindingName));
        }

        return ret;
    }

    public static String getFilterForBindingName(TupleQueryResult result, String bindingName)
            throws QueryEvaluationException
    {
        return TupleQueryResultUtil.getFilterForBindingName(result, bindingName, false);
    }

    public static String getFilterForBindingName(TupleQueryResult result, String bindingName, boolean exclusive)
        throws QueryEvaluationException
    {
        String filterExpression = "FILTER (?" +bindingName+ (exclusive ? " NOT" : "") +" IN (";
        Iterator<Value> subPropIt = getValuesForBindingName(result, bindingName).iterator();
        while (subPropIt.hasNext()) {
            filterExpression += "<"+ subPropIt.next().stringValue() +">"+ (subPropIt.hasNext() ? "," : "))");
        }
        return filterExpression;
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

    public static Collection<Tuple<Value>> createCollectionOfValuePairs(TupleQueryResult result, String value1, String value2)
        throws OpenRDFException
    {
        Collection<Tuple<Value>> resultCollection = new ArrayList<Tuple<Value>>();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Value concept1 = queryResult.getValue(value1);
            Value concept2 = queryResult.getValue(value2);

            resultCollection.add(new Tuple<Value>(concept1, concept2));
        }

        return resultCollection;
    }

}
