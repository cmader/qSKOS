package at.ac.univie.mminf.qskos4j.util.measureinvocation;

@SuppressWarnings("serial")
public class QSKOSMethodInvocationException extends Exception {

	private String methodName;
	
	public QSKOSMethodInvocationException(String methodName) {
		this.methodName = methodName;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
}
