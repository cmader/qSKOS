package at.ac.univie.mminf.qskos4j.result;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.security.cert.CRLSelector;
import java.util.Collection;
import java.util.Collections;

public abstract class Result<T> {

    public enum ReportFormat {TXT, DOT, RDF, PDF}
    public enum ReportStyle {SHORT, EXTENSIVE}
    private T data;

	protected Result(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

    public final void generateReport(BufferedWriter writer, ReportFormat format, ReportStyle style) throws IOException
    {
        switch (format) {
            case TXT:
                generateTextReport(writer, style);
                break;

            case DOT:
                generateDotReport(writer);

            default:
                throw new UnsupportedReportFormatException(format);
        }
    }

    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException {
        throw new NotImplementedException();
    }

	public void generateDotReport(BufferedWriter writer) throws IOException {
        throw new NotImplementedException();
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
