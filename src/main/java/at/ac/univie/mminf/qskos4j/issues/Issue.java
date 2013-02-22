package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.progress.StubProgressMonitor;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;

public abstract class Issue<T extends Report<?>>  {

    public enum IssueType {STATISTICAL, ANALYTICAL}

    protected RepositoryConnection repCon;

    protected IProgressMonitor progressMonitor;

    private String id, name, description;
    private IssueType type;
    private T report;

    protected Issue(RepositoryConnection repCon, String id, String name, String description, IssueType type)
    {
        this.repCon = repCon;
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        progressMonitor = new StubProgressMonitor();
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

    public final RepositoryConnection getRepositoryConnection() {
        return repCon;
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
