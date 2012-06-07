package at.ac.univie.mminf.qskos4j.cmd;

@SuppressWarnings("serial")
public class UnsupportedMeasureIdException extends Exception {
	
	private String unsupportedId;
	
	public UnsupportedMeasureIdException(String unsupportedId) {
		this.unsupportedId = unsupportedId;
	}
	
	public String getUnsupportedId() {
		return unsupportedId;
	}
}
