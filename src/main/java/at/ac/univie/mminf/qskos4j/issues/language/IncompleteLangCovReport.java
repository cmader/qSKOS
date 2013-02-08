package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class IncompleteLangCovReport extends Report<Map<Value, Collection<String>>> {

	IncompleteLangCovReport(Map<Value, Collection<String>> data) {
		super(data);
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionReport<Value>(getData().keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveTextReport());
                break;
        }

    }

	private String generateExtensiveTextReport() {
        StringBuilder extensiveReport = new StringBuilder();

        for (Entry<Value, Collection<String>> entry : getData().entrySet()) {
			extensiveReport.append("concept: '").append(entry.getKey().stringValue()).append("', not covered languages: ").append(entry.getValue().toString()).append("\n");
		}

		return extensiveReport.toString();
	}

}
