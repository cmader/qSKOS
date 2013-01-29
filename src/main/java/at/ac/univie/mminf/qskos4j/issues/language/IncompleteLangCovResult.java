package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class IncompleteLangCovResult extends Result<Map<Value, Collection<String>>> {

	IncompleteLangCovResult(Map<Value, Collection<String>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<Value>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
        StringBuilder extensiveReport = new StringBuilder();

        for (Entry<Value, Collection<String>> entry : getData().entrySet()) {
			extensiveReport.append("concept: '").append(entry.getKey().stringValue()).append("', not covered languages: ").append(entry.getValue().toString()).append("\n");
		}

		return extensiveReport.toString();
	}

}
