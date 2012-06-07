package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public abstract class Issue {

	protected VocabRepository vocabRepository;
	protected IProgressMonitor progressMonitor;
	
	protected Issue() {
	}
	
	protected Issue(VocabRepository vocabRepository) {
		this.vocabRepository = vocabRepository;
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
}
