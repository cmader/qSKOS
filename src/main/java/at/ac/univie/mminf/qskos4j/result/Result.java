package at.ac.univie.mminf.qskos4j.result;

public abstract class Result<T> {

	private T data;
	
	public Result(T data) {
		this.data = data;
	}
	
	public T getData() {
		return data;
	}
	
	public abstract String getShortReport();
	
	public abstract String getExtensiveReport();
	
	@Override
	public String toString() {
		return getShortReport();
	}
	
}
