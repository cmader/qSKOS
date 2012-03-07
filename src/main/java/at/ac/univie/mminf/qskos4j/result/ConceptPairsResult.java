package at.ac.univie.mminf.qskos4j.result;

import java.util.Collection;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class ConceptPairsResult extends Result<Collection<Pair<URI>>> {

	public ConceptPairsResult(Collection<Pair<URI>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		Collection<URI> distinctConcepts = DistinctConceptFinder.getDistinctConceptsFromPairs(getData());
		return "concept count: " +distinctConcepts.size();		
	}
	
	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
