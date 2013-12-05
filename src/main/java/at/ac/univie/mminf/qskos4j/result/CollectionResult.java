package at.ac.univie.mminf.qskos4j.result;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class CollectionResult<T> extends Result<Collection<T>> {

    public CollectionResult(Collection<T> data) {
        super(data);
    }

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                osw.write("count: " + getData().size());
                break;

            case EXTENSIVE:
                osw.write(generateExtensiveTextReport());
                break;
        }
    }

    @Override
    public boolean indicatesProblem() {
        return !getData().isEmpty();
    }

    public String generateExtensiveTextReport() {
        StringBuilder report = new StringBuilder();
        Iterator<T> dataIt = getData().iterator();
        while (dataIt.hasNext()) {
            report.append(dataIt.next().toString()).append(dataIt.hasNext() ? "\n" : "");
        }

        return report.toString();
	}

}
