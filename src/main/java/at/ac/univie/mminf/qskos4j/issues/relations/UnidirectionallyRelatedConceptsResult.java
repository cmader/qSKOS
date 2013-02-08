package at.ac.univie.mminf.qskos4j.issues.relations;

import at.ac.univie.mminf.qskos4j.result.CollectionReport;
import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.Pair;
import org.openrdf.model.Resource;

import java.util.Map;

public class UnidirectionallyRelatedConceptsResult extends Result<Map<Pair<Resource>, String>> {

	public UnidirectionallyRelatedConceptsResult(Map<Pair<Resource>, String> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionReport<Pair<Resource>>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		StringBuilder extensiveReport = new StringBuilder();
		
		for (Pair<Resource> concepts : getData().keySet()) {
			extensiveReport.append("concepts: ").append(concepts.toString()).append(", related by: '").append(getData().get(concepts)).append("'\n");
		}

		return extensiveReport.toString();
	}

}
