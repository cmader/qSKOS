package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Map;

import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;

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
		String extensiveReport = "";
		
		for (Pair<Resource> concepts : getData().keySet()) {
			extensiveReport += "concepts: " +concepts.toString()+ ", related by: '" +getData().get(concepts)+ "'\n";
		}

		return extensiveReport;
	}

}
