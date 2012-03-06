package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import at.ac.univie.mminf.qskos4j.util.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public abstract class Criterion {

	protected VocabRepository vocabRepository;
	private IProgressMonitor progressMonitor;
	
	public Criterion(VocabRepository vocabRepository) {
		this.vocabRepository = vocabRepository;
	}
	
	protected <T> Iterator<T> getMonitoredIterator(
		String taskDescription,	
		Collection<T> collection) 
	{
		if (progressMonitor != null) {
			progressMonitor.setTaskDescription(taskDescription);
		}
		return getMonitoredIterator(collection);
	}
	
	protected <T> Iterator<T> getMonitoredIterator(Collection<T> collection) {
		if (progressMonitor == null) {
			return collection.iterator();
		}
		else {
			progressMonitor.reset();
			return new MonitoredIterator<T>(collection.iterator(), collection.size());
		}
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
		
	private class MonitoredIterator<T> implements Iterator<T> {

		private Iterator<T> delegate;
		private int totalSteps, currentStep;
		
		MonitoredIterator(Iterator<T> delegate, int totalSteps) {
			this.delegate = delegate;
			this.totalSteps = totalSteps;
			currentStep = 0;
		}
		
		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public T next() {
			progressMonitor.onUpdateProgress((float) currentStep / totalSteps);
			
			T nextElement = delegate.next();
			currentStep++;
			
			return nextElement;
		}

		@Override
		public void remove() {
			delegate.remove();
		}
		
	}
	
}
