package at.ac.univie.mminf.qskos4j.result;

public class NumberResult<T extends Number> extends Result<T> {

	public NumberResult(T data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return "value: " +getData().toString();
	}

	@Override
	public String getDetailedReport() {
		// not needed for this type
		return "";
	}

}
