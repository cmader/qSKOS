package at.ac.univie.mminf.qskos4j.util.measureinvocation;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class MeasureInvoker {

    private final Logger logger = LoggerFactory.getLogger(MeasureInvoker.class);
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

                    logger.debug("invoking method '" +method.getName()+ "'");
                    Object invocationResult = method.invoke(qskos);
                    logger.debug("invocation done");

                    return (Result<?>) invocationResult;
                }
            }
        }
        catch (Exception e) {
            throw new QSKOSMethodInvocationException(methodName, e);
        }
        throw new QSKOSMethodInvocationException(methodName, "Method not found");
    }

}
