package at.ac.univie.mminf.qskos4j;

import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;

public class AciResult extends Result<Map<URI, Set<URI>>> {

	public AciResult(Map<URI, Set<URI>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		long referencingResourcesCount = 0;
		
		float avgConceptRank = 0;
		if (getData().size() != 0) {
			avgConceptRank = (float) referencingResourcesCount / (float) getData().size();
		}
		
		return "value: " +avgConceptRank;
	}

	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
