package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import org.eclipse.rdf4j.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class UnidirectionallyRelatedConceptsResult extends Result<Map<Tuple<Resource>, String>> {

    protected UnidirectionallyRelatedConceptsResult(Map<Tuple<Resource>, String> data) {
        super(data);
    }

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionResult<Tuple<Resource>>(getData().keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveReport());
                break;
        }
    }

    private String generateExtensiveReport()
    {
        StringBuilder extensiveReport = new StringBuilder();

        for (Tuple<Resource> concepts : getData().keySet()) {
            extensiveReport.append("concepts: ").append(concepts.toString()).append(", related by: '").append(getData().get(concepts)).append("'\n");
        }

        return extensiveReport.toString();
    }

    @Override
    public long occurrenceCount() {
        return getData().size();
    }
}
