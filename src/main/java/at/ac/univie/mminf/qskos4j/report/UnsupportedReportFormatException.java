package at.ac.univie.mminf.qskos4j.report;

public class UnsupportedReportFormatException extends RuntimeException {

    private final static String MESSAGE = "Unsupported Report Format: '";

    public UnsupportedReportFormatException(String reportFormat) {
        super(MESSAGE +reportFormat+ "'");
    }

    public UnsupportedReportFormatException(Report.ReportFormat format) {
        super(MESSAGE +format.toString()+ "'");
    }

}
