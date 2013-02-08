package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.CollectionReport;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.Map;

public class MissingLangTagResult extends Result<Map<Resource, Collection<Literal>>> {

	MissingLangTagResult(Map<Resource, Collection<Literal>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionReport<Resource>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
        StringBuilder extensiveReport = new StringBuilder();
		
		for (Resource resource : getData().keySet()) {
			Collection<Literal> affectedLiterals = getData().get(resource);
			
			extensiveReport.append("resource: '").append(resource).append("', affected literals: ").append(affectedLiterals.toString()).append("\n");
		}
		
		return extensiveReport.toString();
	}

}
