package at.ac.univie.mminf.qskos4j.report;

public class UnsupportedReportFormatException extends RuntimeException {

    public UnsupportedReportFormatException(Report.ReportFormat format) {
        super("Unsupported Report Format: '" +format.toString()+ "'");
    }

}
