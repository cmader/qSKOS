package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;

public class IllegalResourceResult extends Result<Map<URI, Collection<URI>>> {

	public IllegalResourceResult(Map<URI, Collection<URI>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<URI>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		String extensiveReport = "";
		
		for (URI property : getData().keySet()) {
			extensiveReport += "illegal resource: '" +property.stringValue()+ "', used by " +getData().get(property)+ "\n";
		}
		
		return extensiveReport;
	}

}
