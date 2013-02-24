package at.ac.univie.mminf.qskos4j.report;

import java.io.BufferedWriter;
import java.io.IOException;

public class NumberReport<T> extends Report {

    private T data;

	public NumberReport(T data) {
		this.data = data;
	}

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                osw.write("value: " +data.toString());
                break;

            case EXTENSIVE:
                // not needed for this type
        }
    }

}
