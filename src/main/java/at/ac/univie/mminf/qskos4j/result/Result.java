package at.ac.univie.mminf.qskos4j.result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

public abstract class Result<T> {

    public enum ReportFormat {TXT, DOT}
    public enum ReportStyle {SHORT, EXTENSIVE}

    private T data;

    protected Result(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

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

    public long occurrenceCount() {
        throw new UnsupportedOperationException();
    }

    public boolean isProblematic() {
        return occurrenceCount() != 0;
    }
	
}
