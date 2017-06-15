package at.ac.univie.mminf.qskos4j.issues.labels.util;

import at.ac.univie.mminf.qskos4j.result.Result;
import org.eclipse.rdf4j.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class EmptyLabelsResult extends Result<Map<Resource, Collection<LabelType>>> {

    public EmptyLabelsResult(Map<Resource, Collection<LabelType>> data) {
        super(data);
    }

    @Override
    public void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException {
        if (style == ReportStyle.SHORT) {
            writer.write("count: " +occurrenceCount()+ "\n");
        }
        else if (style == ReportStyle.EXTENSIVE) {
            generateExtensiveTextReport(writer);
        }
    }

    @Override
    public long occurrenceCount() {
        return getData().keySet().size();
    }

    private void generateExtensiveTextReport(BufferedWriter writer) throws IOException {
        for (Resource resource : getData().keySet()) {
            writer.write(resource.stringValue() +": "+ getData().get(resource).toString() +"\n");
        }
    }
}
