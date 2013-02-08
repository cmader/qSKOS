package at.ac.univie.mminf.qskos4j.report;

import java.io.BufferedWriter;
import java.io.IOException;

public class NumberReport<T extends Number> extends Report<T> {

	public NumberReport(T data) {
		super(data);
	}

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                osw.write("value: " +getData().toString());
                break;

            case EXTENSIVE:
                // not needed for this type
        }
    }

}
