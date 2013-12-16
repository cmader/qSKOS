package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class IncompleteLangCovReport extends Report {

    private Map<Value, Collection<String>> data;

	IncompleteLangCovReport(Map<Value, Collection<String>> data) {
		this.data = data;
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionReport<Value>(data.keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveTextReport());
                break;
        }

    }

	private String generateExtensiveTextReport() {
        StringBuilder extensiveReport = new StringBuilder();

        Iterator<Entry<Value, Collection<String>>> entryIt = data.entrySet().iterator();
        while (entryIt.hasNext()) {
            Entry<Value, Collection<String>> entry = entryIt.next();
            extensiveReport.append("concept: '")
                           .append(entry.getKey().stringValue())
                           .append("', not covered languages: ")
                           .append(entry.getValue().toString())
                           .append(entryIt.hasNext() ? "\n" : "");
        }

        for (Entry<Value, Collection<String>> entry : data.entrySet()) {

		}

		return extensiveReport.toString();
	}

}
