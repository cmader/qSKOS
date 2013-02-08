package at.ac.univie.mminf.qskos4j.result;

import at.ac.univie.mminf.qskos4j.result.Result;

import java.io.BufferedWriter;
import java.io.IOException;

public class NumberResult<T extends Number> extends Result<T> {

	public NumberResult(T data) {
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
