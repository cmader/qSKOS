package at.ac.univie.mminf.qskos4j.cmd;

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
