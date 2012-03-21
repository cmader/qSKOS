package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;

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
		String extensiveReport = "";
		
		for (Resource resource : getData().keySet()) {
			Collection<String> missingLangs = getData().get(resource);
			
			extensiveReport += "concept: '" +resource.stringValue()+ "', not covered languages: " +missingLangs.toString()+ "\n";
		}

		return extensiveReport;
	}

}
