package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public abstract class Issue {

	protected VocabRepository vocabRepository;
	protected IProgressMonitor progressMonitor;
    private String id, name, description;
	
	protected Issue(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
	}

    public abstract Result<?> invoke();

	public void setVocabRepository(VocabRepository vocabRepository) {
		this.vocabRepository = vocabRepository;
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
}
