package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.progress.StubProgressMonitor;
import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public abstract class Issue<T extends Result<?>> {

    private IssueDescriptor issueDescriptor;
    protected RepositoryConnection repCon;
    protected IProgressMonitor progressMonitor = new StubProgressMonitor();
    private T result;

    public Issue(IssueDescriptor issueDescriptor) {
        this.issueDescriptor = issueDescriptor;
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
        if (issueDescriptor.getDependentIssue() != null) {
            issueDescriptor.getDependentIssue().setRepositoryConnection(repCon);
        }

        this.repCon = repCon;
        reset();
    }

    public IssueDescriptor getIssueDescriptor() {
        return issueDescriptor;
    }

    @Override
    public String toString() {
        return "Issue{" +
            "id='" + issueDescriptor.getId() + '\'' +
            ", name='" + issueDescriptor.getName() + '\'' +
            ", description='" + issueDescriptor.getDescription() + '\'' +
            ", type=" + issueDescriptor.getType() +
            '}';
    }
}
