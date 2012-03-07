package at.ac.univie.mminf.qskos4j.result;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class RarResult extends Result<Map<URI, Set<Pair<URI>>>> {

	public RarResult(Map<URI, Set<Pair<URI>>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return "pair count: "+getPairCount()+ "\nconcept count: " +getConceptCount();
	}
	
	private long getPairCount() {
		long pairCount = 0;
		
		if (!getData().isEmpty()) {			
			for (Set<Pair<URI>> pairs : getData().values()) {
				pairCount += pairs.size();
			}
		}
		return pairCount;
	}
		
	private int getConceptCount() {
		Set<URI> involvedConcepts = new HashSet<URI>();
		for (Set<Pair<URI>> conceptPairs : getData().values()) {
			involvedConcepts.addAll(DistinctConceptFinder.getDistinctConceptsFromPairs(conceptPairs));	
		}
		
		return involvedConcepts.size();
	}

	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
