package at.ac.univie.mminf.qskos4j.result;

import java.util.Collection;
import java.util.Collections;

public abstract class Result<T> {

	private T data;

	protected Result(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}
		
	public abstract String getShortReport();
	
	public abstract String getExtensiveReport();
	
	public Collection<String> getAsDOT() {
		// implement depending on T
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return getShortReport();
	}
	
}
