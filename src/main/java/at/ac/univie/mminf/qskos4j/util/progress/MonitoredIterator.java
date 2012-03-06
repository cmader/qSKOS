package at.ac.univie.mminf.qskos4j.util.progress;

import java.util.Collection;
import java.util.Iterator;

public class MonitoredIterator<T> implements Iterator<T> {

	private Iterator<T> delegate;
	private int totalSteps, currentStep;
	private IProgressMonitor progressMonitor;
	
	public MonitoredIterator(
		Collection<T> iteratorProvider,
		IProgressMonitor progressMonitor) 
	{
		this.delegate = iteratorProvider.iterator();
		this.totalSteps = iteratorProvider.size();
		this.progressMonitor = progressMonitor;
		
		currentStep = 0;
		progressMonitor.reset();	
	}
	
	public MonitoredIterator(
		Collection<T> iteratorProvider,
		IProgressMonitor progressMonitor,
		String taskDescription) 
	{
		this(iteratorProvider, progressMonitor);
		progressMonitor.setTaskDescription(taskDescription);
	}
			
	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public T next() {
		if (progressMonitor != null) {
			progressMonitor.onUpdateProgress((float) currentStep / totalSteps);
			currentStep++;	
		}
		
		return delegate.next();
	}

	@Override
	public void remove() {
		delegate.remove();
	}
	
}
