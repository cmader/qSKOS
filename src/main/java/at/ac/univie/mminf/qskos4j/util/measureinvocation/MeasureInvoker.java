package at.ac.univie.mminf.qskos4j.util.measureinvocation;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.result.Result;

import java.lang.reflect.Method;
import java.util.*;

public class MeasureInvoker {

    private QSkos qskos;

    public MeasureInvoker(QSkos qskos) {
        this.qskos = qskos;
    }

    public Map<String, Result<?>> checkForAllMeasures()
            throws UnsupportedMeasureIdException, QSKOSMethodInvocationException
    {
        return checkForMeasures(Arrays.asList(MeasureDescription.values()));
    }

    public Map<String, Result<?>> checkForMeasures(List<MeasureDescription> measures)
            throws UnsupportedMeasureIdException, QSKOSMethodInvocationException
    {
        Map<String, Result<?>> measureIdToResultsMap = new HashMap<String, Result<?>>();

        for (MeasureDescription measure : measures) {
            System.out.println("--- " +measure.getName());
            String qSkosMethodName = measure.getQSkosMethodName();

            Result<?> result = invokeQSkosMethod(qSkosMethodName);
            measureIdToResultsMap.put(measure.getId(), result);
        }

        return measureIdToResultsMap;
    }

    private Result<?> invokeQSkosMethod(String methodName)
            throws QSKOSMethodInvocationException
    {
        try {
            for (Method method : qskos.getClass().getMethods()) {
                if (method.getName().equals(methodName)) {
                    return (Result<?>) method.invoke(qskos);
                }
            }
        }
        catch (Exception e) {
            // fall through
        }
        throw new QSKOSMethodInvocationException(methodName);
    }

}
