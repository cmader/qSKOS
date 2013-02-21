package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.progress.StubProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;

public abstract class Issue<T extends Report<?>>  {

    public enum IssueType {STATISTICAL, ANALYTICAL}

    @Deprecated
	protected VocabRepository vocabRepository;
    protected Repository repository;

    protected IProgressMonitor progressMonitor;

    private String id, name, description;
    private IssueType type;
    private T report;

    protected Issue(Repository repository, String id, String name, String description, IssueType type)
    {
        this.repository = repository;
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        progressMonitor = new StubProgressMonitor();
    }

    @Deprecated
    protected Issue(VocabRepository vocabRepository, String id, String name, String description, IssueType type)
    {
        this(vocabRepository.getRepository(), id, name, description, type);
        this.vocabRepository = vocabRepository;
    }

    protected abstract T invoke() throws OpenRDFException;

    public final T getReport() throws OpenRDFException {
        if (report == null) {
            report = invoke();
        }
        return report;
    }

    protected final void reset() {
        report = null;
        if (progressMonitor != null) {
            progressMonitor.reset();
        }
    }

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

    @Deprecated
    public final VocabRepository getVocabRepository() {
        return vocabRepository;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public final IssueType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Issue{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", type=" + type +
            '}';
    }
}
