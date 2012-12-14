package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.Map;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;

public class ConceptLabelsResult extends Result<Map<URI, Collection<String>>> {

	public ConceptLabelsResult(Map<URI, Collection<String>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<URI>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		StringBuilder extensiveReport = new StringBuilder();
		
		for (URI concept : getData().keySet()) {
			Collection<String> labels = getData().get(concept);
			
			extensiveReport.append("concept: '" +concept.stringValue()+ "', labels: " +labels.toString()+ "\n");
		}
		
		return extensiveReport.toString();
	}

}
