package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;

public class MissingLangTagResult extends Result<Map<String, Collection<Resource>>> {

	public MissingLangTagResult(Map<String, Collection<Resource>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<String>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		String extensiveReport = "";
		
		for (String literal : getData().keySet()) {
			Collection<Resource> affectedRes = getData().get(literal);
			
			extensiveReport += "literal: '" +literal+ "', affected resources: " +affectedRes.toString()+ "\n";
		}
		
		return extensiveReport;
	}

}
