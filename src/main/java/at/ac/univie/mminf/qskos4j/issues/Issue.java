package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.StubProgressMonitor;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryConnection;

public abstract class Issue<T extends Result<?>> {

    public enum IssueType {STATISTICAL, ANALYTICAL}

    protected IProgressMonitor progressMonitor = new StubProgressMonitor();

    private String id, name, description;
    private IssueType type;
    private URI weblink;

    private Issue(String name, String description, IssueType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public Issue(String id, String name, String description, IssueType type) {
        this(name, description, type);
        this.id = id;
    }

    public Issue(String id, String name, String description, IssueType type, URI weblink) {
        this(id, name, description, type);
        this.weblink = weblink;
    }

    protected abstract T invoke(RepositoryConnection repCon) throws OpenRDFException;

    public final T getResult(RepositoryConnection repCon) throws OpenRDFException {
        if (progressMonitor != null) {
            progressMonitor.reset();
        }

        return invoke(repCon);
    }

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
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

    public URI getWeblink() {
        return weblink;
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
