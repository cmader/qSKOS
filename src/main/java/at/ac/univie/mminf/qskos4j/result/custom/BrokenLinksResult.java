package at.ac.univie.mminf.qskos4j.result.custom;

import java.net.URL;
import java.util.Map;

import at.ac.univie.mminf.qskos4j.result.Result;

public class BrokenLinksResult extends Result<Map<URL, String>> {

	private Float subsetSize_percent;
	
	public BrokenLinksResult(Map<URL, String> data, Float subsetSize_percent) {
		super(data);
		this.subsetSize_percent = subsetSize_percent;
	}

	@Override
	public String getShortReport() {
		long notAvailableCount = 0;
		
		for (URL url : getData().keySet()) {
			String contentType = getData().get(url);
			if (contentType == null) {
				notAvailableCount++;
			}
		}
		
		String report = "broken link count: "+notAvailableCount;
		
		if (subsetSize_percent != null) {
			notAvailableCount *= 100 / subsetSize_percent;
			report += ", extrapolated: " +notAvailableCount;
		}
		
		return report;
	}

	@Override
	public String getExtensiveReport() {
		String extensiveReport = "";
		
		for (URL resource : getData().keySet()) {
			String contentType = getData().get(resource);
			if (contentType == null) {
				contentType = "unavailable";
			}
			
			extensiveReport += "resource: '" +resource.toString()+ "', content type: " +contentType+ "\n";
		}

		return extensiveReport;
	}

}
