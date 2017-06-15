package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.StubProgressMonitor;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public abstract class Issue<T extends Result<?>> {

    public enum IssueType {STATISTICAL, ANALYTICAL}

    protected RepositoryConnection repCon;

    protected IProgressMonitor progressMonitor = new StubProgressMonitor();

    private String id, name, description;
    private IssueType type;
    private T result;
    private Issue dependentIssue;
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

    public Issue(Issue dependentIssue, String id, String name, String description, IssueType type) {
        this(id, name, description, type);
        this.dependentIssue = dependentIssue;
    }

    public Issue(String id, String name, String description, IssueType type, URI weblink) {
        this(id, name, description, type);
        this.weblink = weblink;
    }

    public Issue(Issue dependentIssue, String id, String name, String description, IssueType type, URI weblink) {
        this(dependentIssue, id, name, description, type);
        this.weblink = weblink;
    }

    protected abstract T invoke() throws RDF4JException;

    public final T getResult() throws RDF4JException {
        if (result == null) {
            result = invoke();
        }
        return result;
    }

    protected final void reset() {
        result = null;
        if (progressMonitor != null) {
            progressMonitor.reset();
        }
    }

	public void setProgressMonitor(IProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

    public void setRepositoryConnection(RepositoryConnection repCon) {
        if (dependentIssue != null) {
            dependentIssue.setRepositoryConnection(repCon);
        }

        this.repCon = repCon;
        reset();
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
