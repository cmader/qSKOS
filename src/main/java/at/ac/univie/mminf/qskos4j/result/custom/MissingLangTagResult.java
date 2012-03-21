package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;

public class MissingLangTagResult extends Result<Map<Resource, Collection<Literal>>> {

	public MissingLangTagResult(Map<Resource, Collection<Literal>> data) {
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
			Collection<Literal> affectedLiterals = getData().get(resource);
			
			extensiveReport += "resource: '" +resource+ "', affected literals: " +affectedLiterals.toString()+ "\n";
		}
		
		return extensiveReport;
	}

}
