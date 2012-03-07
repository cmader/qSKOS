package at.ac.univie.mminf.qskos4j.result;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class DistinctConceptFinder {

	public static Collection<URI> getDistinctConceptsFromPairs(Collection<Pair<URI>> conceptURIs) {
		Set<URI> distinctConcepts = new HashSet<URI>();
		
		for (Pair<URI> pair : conceptURIs) {
			distinctConcepts.add(pair.getFirst());
			distinctConcepts.add(pair.getSecond());
		}
		
		return distinctConcepts;
	}
	
}
