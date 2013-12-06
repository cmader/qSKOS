package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class OmittedOrInvalidLanguageTagsResult extends Result<Map<Resource, Collection<Literal>>> {

    OmittedOrInvalidLanguageTagsResult(Map<Resource, Collection<Literal>> data) {
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

        Iterator<Map.Entry<Resource, Collection<Literal>>> entryIt = getData().entrySet().iterator();
        while (entryIt.hasNext()) {
            Map.Entry<Resource, Collection<Literal>> entry = entryIt.next();
            extensiveReport.append("concept: '")
                    .append(entry.getKey().stringValue())
                    .append("', literals with no or invalid language tags: ")
                    .append(entry.getValue().toString())
                    .append(entryIt.hasNext() ? "\n" : "");
        }

        return extensiveReport.toString();
    }

    @Override
    public boolean indicatesProblem() {
        return !getData().isEmpty();
    }
}
