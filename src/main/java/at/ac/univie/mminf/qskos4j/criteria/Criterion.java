package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public abstract class Criterion {

	protected VocabRepository vocabRepository;
	protected IProgressMonitor progressMonitor;
	
	public Criterion(VocabRepository vocabRepository) {
		this.vocabRepository = vocabRepository;
	}
	
	protected <T> Set<T> createRandomSubset(
		Set<T> superSet, 
		float randomSubsetSize_percent) 
	{
		int subsetElementCount = (int) ((float) (superSet.size() * randomSubsetSize_percent) / (float) 100);
		Set<T> subset = new HashSet<T>(subsetElementCount);
		Random randomGenerator = new Random();
		
		List<T> origURIs = new ArrayList<T>(superSet);
		for (int i = 0; i < subsetElementCount; i++) {
			T randomElement;
			do {
				int origUriIndex = randomGenerator.nextInt(superSet.size());
				randomElement = origURIs.get(origUriIndex);
			}
			while (subset.contains(randomElement));
			subset.add(randomElement);			
		}
		
		return new HashSet<T>(subset);
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
		
}
