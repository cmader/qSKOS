package at.ac.univie.mminf.qskos4j.result.general;

import at.ac.univie.mminf.qskos4j.result.Result;

public class NumberResult<T extends Number> extends Result<T> {

	public NumberResult(T data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return "value: " +getData().toString();
	}

	@Override
	public String getExtensiveReport() {
		// not needed for this type
		return "";
	}

}
