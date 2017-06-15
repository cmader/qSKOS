package at.ac.univie.mminf.qskos4j.util;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

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

    public static long countResults(TupleQueryResult result) throws QueryEvaluationException {
        long count = 0;

        while (result.hasNext()) {
            count++;
            result.next();
        }

        return count;
    }

    public static Collection<Tuple<Resource>> createCollectionOfResourcePairs(TupleQueryResult result, String value1, String value2)
        throws RDF4JException
    {
        Collection<Tuple<Resource>> resultCollection = new ArrayList<Tuple<Resource>>();

        while (result.hasNext()) {
            BindingSet queryResult = result.next();
            Resource concept1 = (Resource) queryResult.getValue(value1);
            Resource concept2 = (Resource) queryResult.getValue(value2);

            resultCollection.add(new Tuple<Resource>(concept1, concept2));
        }

        return resultCollection;
    }

}
