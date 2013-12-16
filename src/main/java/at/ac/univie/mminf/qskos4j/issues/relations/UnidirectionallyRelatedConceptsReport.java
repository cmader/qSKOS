package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Pair;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class UnidirectionallyRelatedConceptsReport extends Report {

    private Map<Pair<Resource>, String> data;

	public UnidirectionallyRelatedConceptsReport(Map<Pair<Resource>, String> data) {
		this.data = data;
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionReport<Pair<Resource>>(data.keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveReport());
                break;
        }
    }

    private String generateExtensiveReport()
    {
		StringBuilder extensiveReport = new StringBuilder();
		
		for (Pair<Resource> concepts : data.keySet()) {
			extensiveReport.append("concepts: ").append(concepts.toString()).append(", related by: '").append(data.get(concepts)).append("'\n");
		}

		return extensiveReport.toString();
	}

}
