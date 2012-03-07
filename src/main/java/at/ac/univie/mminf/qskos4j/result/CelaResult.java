package at.ac.univie.mminf.qskos4j.result;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.URI;

public class CelaResult extends Result<Map<URI, List<URL>>> {

	public CelaResult(Map<URI, List<URL>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		List<URL> allExtUrls = new ArrayList<URL>();
		for (List<URL> extUrls : getData().values()) {
			allExtUrls.addAll(extUrls);
		}
		
		float extLinkAvg = (float) allExtUrls.size() / (float) getData().keySet().size();
		return "value: " +extLinkAvg;
	}

	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
