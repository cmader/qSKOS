package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class IncompleteLangCovResult extends Result<Map<Value, Collection<String>>> {

    IncompleteLangCovResult(Map<Value, Collection<String>> data) {
		super(data);
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionResult<Value>(getData().keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveTextReport());
                break;
        }

    }

	private String generateExtensiveTextReport() {
        StringBuilder extensiveReport = new StringBuilder();

        Iterator<Entry<Value, Collection<String>>> entryIt = getData().entrySet().iterator();
        while (entryIt.hasNext()) {
            Entry<Value, Collection<String>> entry = entryIt.next();
            extensiveReport.append("concept: '")
                           .append(entry.getKey().stringValue())
                           .append("', not covered languages: ")
                           .append(entry.getValue().toString())
                           .append(entryIt.hasNext() ? "\n" : "");
        }

        for (Entry<Value, Collection<String>> entry : getData().entrySet()) {

		}

		return extensiveReport.toString();
	}

    @Override
    public boolean indicatesProblem() {
        return !getData().isEmpty();
    }

}
