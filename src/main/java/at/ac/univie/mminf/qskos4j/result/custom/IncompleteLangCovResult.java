package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;

public class IncompleteLangCovResult extends Result<Map<Resource, Collection<String>>> {

	public IncompleteLangCovResult(Map<Resource, Collection<String>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<Resource>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
        StringBuilder extensiveReport = new StringBuilder();

        for (Entry<Resource, Collection<String>> entry : getData().entrySet()) {
			extensiveReport.append("concept: '" +entry.getKey().stringValue()+ "', not covered languages: " +entry.getValue().toString()+ "\n");
		}

		return extensiveReport.toString();
	}

}
