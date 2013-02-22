package at.ac.univie.mminf.qskos4j.issues.pp.adhoc;

import at.ac.univie.mminf.qskos4j.issues.pp.adhoc.exceptions.IssueDetectedException;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;

public interface AdHocCheckable {

    public abstract void checkStatement(Statement statement) throws IssueDetectedException, OpenRDFException;

}
