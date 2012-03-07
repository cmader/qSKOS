package at.ac.univie.mminf.qskos4j.result;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;

public class WccResult extends Result<List<Set<URI>>> {

	public WccResult(List<Set<URI>> data) {
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
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
