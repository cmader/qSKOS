package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class IncompleteLangCovResult extends Result<Map<Resource, Collection<String>>> {

    IncompleteLangCovResult(Map<Resource, Collection<String>> data) {
		super(data);
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionResult<Resource>(getData().keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveTextReport());
                break;
        }

    }

	private String generateExtensiveTextReport() {
        StringBuilder extensiveReport = new StringBuilder();

        Iterator<Entry<Resource, Collection<String>>> entryIt = getData().entrySet().iterator();
        while (entryIt.hasNext()) {
            Entry<Resource, Collection<String>> entry = entryIt.next();
            extensiveReport.append("concept: '")
                           .append(entry.getKey().stringValue())
                           .append("', not covered languages: ")
                           .append(entry.getValue().toString())
                           .append(entryIt.hasNext() ? "\n" : "");
        }

		return extensiveReport.toString();
	}

    @Override
    public long occurrenceCount() {
        return getData().size();
    }

}
