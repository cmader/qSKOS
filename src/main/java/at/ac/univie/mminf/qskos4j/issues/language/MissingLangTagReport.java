package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class MissingLangTagReport extends Report {

    private Map<Resource, Collection<Literal>> data;

	MissingLangTagReport(Map<Resource, Collection<Literal>> data) {
		this.data = data;
	}

    @Override
    protected void generateTextReport(BufferedWriter writer, ReportStyle style) throws IOException
    {
        switch (style) {
            case SHORT:
                new CollectionReport<Resource>(data.keySet()).generateReport(writer, ReportFormat.TXT, ReportStyle.SHORT);
                break;

            case EXTENSIVE:
                writer.write(generateExtensiveReport());
                break;
        }
    }

	private String generateExtensiveReport() {
        StringBuilder extensiveReport = new StringBuilder();
		
		for (Resource resource : data.keySet()) {
			Collection<Literal> affectedLiterals = data.get(resource);
			
			extensiveReport.append("resource: '").append(resource).append("', affected literals: ").append(affectedLiterals.toString()).append("\n");
		}
		
		return extensiveReport.toString();
	}

}
