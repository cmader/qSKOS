package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;

public class WeaklyConnectedComponentsResult extends Result<List<Set<URI>>> {

	public WeaklyConnectedComponentsResult(List<Set<URI>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		long componentCount = 0;
		
		for (Collection<URI> component : getData()) {
			componentCount += component.size() > 1 ? 1 : 0;
		}
		
		return "count: " +componentCount;
	}

	@Override
	public String getExtensiveReport() {
		String detailedReport = "";
		long compCount = 1;
		
		for (Set<URI> component : getData()) {
			if (component.size() > 1) {
				detailedReport += "component " +compCount+ ": " +component.toString()+ "\n";
				compCount++;
			}
		}
		
		return detailedReport;
	}

}
