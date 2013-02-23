package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.progress.IProgressMonitor;
import at.ac.univie.mminf.qskos4j.util.progress.StubProgressMonitor;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;

public abstract class Issue<T> {

    public enum IssueType {STATISTICAL, ANALYTICAL}

    protected RepositoryConnection repCon;

    protected IProgressMonitor progressMonitor = new StubProgressMonitor();

    private String id, name, description;
    private IssueType type;
    private T preparedData;

    private Issue(String id, String name, String description, IssueType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    /**
     * Use this constructor to re-use already prepared data
     */
    protected Issue(T preparedData, String id, String name, String description, IssueType type) {
        this(id, name, description, type);
        this.preparedData = preparedData;
    }

    /**
     * Use this constructor to fetch RDF data and prepare the data yourself
     */
    protected Issue(RepositoryConnection repCon, String id, String name, String description, IssueType type)
    {
        this(id, name, description, type);
        this.repCon = repCon;
    }

    protected abstract T prepareData() throws OpenRDFException;
    protected abstract Report prepareReport(T preparedData);

    public final T getPreparedData() throws OpenRDFException {
        if (preparedData == null) {
            preparedData = prepareData();
        }
        return preparedData;
    }

    public final Report getReport() throws OpenRDFException
    {
        return prepareReport(getPreparedData());
    }

    protected final void reset() {
        preparedData = null;
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
