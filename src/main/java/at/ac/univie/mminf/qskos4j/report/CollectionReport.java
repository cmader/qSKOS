package at.ac.univie.mminf.qskos4j.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Report class that holds a collection of objects of interest
 * 
 * @author christian
 *
 * @param <T> type of the collection's content
 */
public class CollectionReport<T> extends Report {

    private Collection<T> data;

	public CollectionReport(Collection<T> data) {
		this.data = data;
	}

    @Override
    protected void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                osw.write("count: " + data.size());
                break;

            case EXTENSIVE:
                osw.write(generateExtensiveTextReport());
                break;
        }
    }

	public String generateExtensiveTextReport() {
        StringBuilder report = new StringBuilder();
        Iterator<T> dataIt = data.iterator();
        while (dataIt.hasNext()) {
            report.append(dataIt.next().toString()).append(dataIt.hasNext() ? "\n" : "");
        }

        return report.toString();
	}

    @Override
    public void generateHtmlReport(BufferedWriter writer, ReportStyle style) throws IOException {
        writer.write("<div class='" +this.getClass().getName()+ "'>");

        switch (style) {
            case SHORT:
                generateTextReport(writer, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                generateExtensiveHtmlReport(writer);
                break;
        }

        writer.write("</div>");
    }

    public void generateExtensiveHtmlReport(BufferedWriter writer) throws IOException {
        for (T collectionElement : data) {
            if (collectionElement instanceof HtmlRenderable) {
                writer.write("<p>" +((HtmlRenderable) collectionElement).toHtml()+ "</p>");
            }
            else {
                throw new UnsupportedOperationException("Collection elements of type " +collectionElement.getClass().getName()+ " have no HTML representation");
            }
        }
    }
}
