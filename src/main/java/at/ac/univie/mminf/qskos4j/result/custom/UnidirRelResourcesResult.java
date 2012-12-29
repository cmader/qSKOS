package at.ac.univie.mminf.qskos4j.result.custom;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import org.openrdf.model.Resource;

import java.util.Map;

public class UnidirRelResourcesResult extends Result<Map<Pair<Resource>, String>> {

	public UnidirRelResourcesResult(Map<Pair<Resource>, String> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<Pair<Resource>>(getData().keySet()).getShortReport();
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
