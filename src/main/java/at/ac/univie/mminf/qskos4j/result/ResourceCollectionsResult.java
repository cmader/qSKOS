package at.ac.univie.mminf.qskos4j.result;

import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class ResourceCollectionsResult extends CollectionResult<Collection<Resource>> {

    private String valueSetName;

    public ResourceCollectionsResult(Collection<Collection<Resource>> data, String valueSetName) {
        super(data);
        this.valueSetName = valueSetName;
    }

    @Override
    public void generateTextReport(BufferedWriter osw, ReportStyle style) throws IOException {
        StringBuilder report = new StringBuilder();
        long compCount = 1;

        if (style == ReportStyle.SHORT) {
            report.append("count: ").append(getData().size()).append("\n");
        }

        Iterator<Collection<Resource>> componentIt = getData().iterator();
        while (componentIt.hasNext()) {
            Collection<Resource> component = componentIt.next();

            report.append(valueSetName +" ").append(compCount).append(", size: ").append(component.size());
            if (style == ReportStyle.EXTENSIVE) {
                for (Value value : component) {
                    report.append("\n\t").append(value.toString());
                }
            }
            compCount++;

            if (componentIt.hasNext()) report.append("\n");
        }

        osw.write(report.toString());
    }

}
