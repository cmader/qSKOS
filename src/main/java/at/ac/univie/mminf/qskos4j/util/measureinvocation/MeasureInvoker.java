package at.ac.univie.mminf.qskos4j.util.measureinvocation;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.result.Result;

import java.lang.reflect.Method;

public class MeasureInvoker {

    private QSkos qskos;

    public MeasureInvoker(QSkos qskos) {
        this.qskos = qskos;
    }

    public Result<?> getMeasureResult(MeasureDescription measure)
        throws QSKOSMethodInvocationException
    {
        return invokeQSkosMethod(measure.getQSkosMethodName());
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
