package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.util.Collection;
import java.util.Collections;

public abstract class Issue<T extends Result<?>> {

    public enum IssueType {STATISTICAL, ANALYTICAL}

	protected VocabRepository vocabRepository;
	protected IProgressMonitor progressMonitor;

    private String id, name, description;
    private IssueType type;
    private T result;

    protected Issue(String id, String name, String description, IssueType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    protected abstract T invoke() throws OpenRDFException;

    public final T getResult() throws OpenRDFException {
        if (result == null) {
            result = invoke();
        }
        return result;
    }

    public final void reset() {
        result = null;
    }

	public void setVocabRepository(VocabRepository vocabRepository) {
		this.vocabRepository = vocabRepository;
	}
	
	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

}
