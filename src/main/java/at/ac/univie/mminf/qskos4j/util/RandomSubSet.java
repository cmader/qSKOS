package at.ac.univie.mminf.qskos4j.util;

import java.util.*;

public class RandomSubSet<T> extends AbstractSet<T>{

	private Collection<T> subset;
	private List<T> origElements;
	private int subsetElementCount;
	
	public RandomSubSet(Collection<T> origSet, float randomSubsetSize_percent) 
	{
		origElements = new ArrayList<T>(origSet);	
		subsetElementCount = (int) ((origSet.size() * randomSubsetSize_percent) / (float) 100);
		subset = new HashSet<T>(subsetElementCount);
		
		pickRandomElements();
	}
	
	private void pickRandomElements() 
	{	
		Random randomGenerator = new Random();
		
		for (int i = 0; i < subsetElementCount; i++) {
			T randomElement;
			do {
				int origUriIndex = randomGenerator.nextInt(origElements.size());
				randomElement = origElements.get(origUriIndex);
			}
			while (subset.contains(randomElement));
			subset.add(randomElement);			
		}
	}

	@Override
	public Iterator<T> iterator() {
		return subset.iterator();
	}

	@Override
	public int size() {
		return subset.size();
	}
	
}
