package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;

public class ResourceLabelsResult extends Result<Map<URI, Collection<String>>> {

	public ResourceLabelsResult(Map<URI, Collection<String>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<URI>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		StringBuilder extensiveReport = new StringBuilder();
		
		for (URI resource : getData().keySet()) {
			Collection<String> labels = getData().get(resource);
			
			extensiveReport.append("resource: '" +resource.stringValue()+ "', labels: " +labels.toString()+ "\n");
		}
		
		return extensiveReport.toString();
	}

}
