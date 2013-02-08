package at.ac.univie.mminf.qskos4j.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Report class that holds a collection of objects of interest
 * 
 * @author christian
 *
 * @param <T> type of the collection's content
 */
public class CollectionReport<T> extends Report<Collection<T>> {

	public CollectionReport(Collection<T> data) {
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

	public String generateExtensiveTextReport() {
        StringBuilder report = new StringBuilder();

        for (T dataItem : getData()) {
            report.append("\n").append(dataItem.toString());
        }

        return report.toString();
	}

}
