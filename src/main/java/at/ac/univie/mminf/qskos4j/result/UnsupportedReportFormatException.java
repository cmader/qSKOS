package at.ac.univie.mminf.qskos4j.result;

public class UnsupportedReportFormatException extends RuntimeException {

    public UnsupportedReportFormatException(Result.ReportFormat format) {
        super("Unsupported Report Format: '" +format.toString()+ "'");
    }

}
