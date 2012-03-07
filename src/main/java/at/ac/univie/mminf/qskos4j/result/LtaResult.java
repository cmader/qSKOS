package at.ac.univie.mminf.qskos4j.result;

import java.net.URL;
import java.util.Map;

public class LtaResult extends Result<Map<URL, String>> {

	private Float subsetSize_percent;
	
	public LtaResult(Map<URL, String> data, Float subsetSize_percent) {
		super(data);
		this.subsetSize_percent = subsetSize_percent;
	}

	@Override
	public String getShortReport() {
		long availableCount = 0, notAvailableCount = 0;
		
		for (URL url : getData().keySet()) {
			String contentType = getData().get(url);
			if (contentType == null) {
				notAvailableCount++;
				System.out.println(url.toString());
			}
			else {
				availableCount++;
			}
		}
		
		if (subsetSize_percent != null) {
			availableCount *= 100 / subsetSize_percent;
			notAvailableCount *= 100 / subsetSize_percent;
		}
		
		return "available: " +availableCount+ "\nnot available: "+notAvailableCount;
	}

	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
