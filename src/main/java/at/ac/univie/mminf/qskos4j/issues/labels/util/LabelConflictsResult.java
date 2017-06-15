package at.ac.univie.mminf.qskos4j.issues.labels.util;

import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import org.eclipse.rdf4j.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class LabelConflictsResult extends CollectionResult<LabelConflict> {

    public LabelConflictsResult(Collection<LabelConflict> data) {
        super(data);
    }

    @Override
    public void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException {
        if (style == ReportStyle.SHORT) {
            writer.write("count: " +getDistinctConcepts().size()+ "\n");
        }
        else if (style == ReportStyle.EXTENSIVE) {
            generateExtensiveTextReport(writer);
        }
    }

    private Collection<Resource> getDistinctConcepts() {
        Collection<Resource> distinctConcepts = new HashSet<Resource>();

        for (LabelConflict labelConflict : getData()) {
            for (Resource labeledConcept : labelConflict.getAffectedResources()) {
                distinctConcepts.add(labeledConcept);
            }
        }

        return distinctConcepts;
    }

    private void generateExtensiveTextReport(BufferedWriter writer) throws IOException {
        for (LabelConflict labelConflict : getData()) {
            writer.write(labelConflict.toString());
            writer.write("\n");
        }
    }

}