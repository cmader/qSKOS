package at.ac.univie.mminf.qskos4j.result;

public class UnsupportedReportFormatException extends RuntimeException {

    private final static String MESSAGE = "Unsupported Result Format: '";

    public UnsupportedReportFormatException(Result.ReportFormat format) {
        super(MESSAGE +format.toString()+ "'");
    }

}
