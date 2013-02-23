package at.ac.univie.mminf.qskos4j.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

public abstract class Report {

    public enum ReportFormat {TXT, DOT, RDF, HTML}
    public enum ReportStyle {SHORT, EXTENSIVE}

    public final void generateReport(BufferedWriter writer, ReportFormat format) throws IOException
    {
        generateReport(writer, format, ReportStyle.SHORT);
    }

    public final void generateReport(BufferedWriter writer, ReportFormat format, ReportStyle style) throws IOException
    {
        switch (format) {
            case TXT:
                generateTextReport(writer, style);
                break;

            case DOT:
                generateDotReport(writer);
                break;

            case HTML:
                generateHtmlReport(writer, style);
                break;

            default:
                throw new UnsupportedReportFormatException(format);
        }
    }

    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException {
        throw new UnsupportedOperationException();
    }

	public void generateDotReport(BufferedWriter writer) throws IOException {
        throw new UnsupportedOperationException();
	}

    public void generateHtmlReport(BufferedWriter writer, ReportStyle style) throws IOException {
        throw new UnsupportedOperationException();
    }
	
	@Override
	public String toString() {
        try {
            BufferedWriter textReportStringWriter = new BufferedWriter(new StringWriter());
            generateTextReport(textReportStringWriter, ReportStyle.SHORT);
            textReportStringWriter.close();
            return textReportStringWriter.toString();
        }
        catch (IOException e) {
            return "Could not create short text report (" +e.getMessage()+ ")";
        }
	}
	
}
