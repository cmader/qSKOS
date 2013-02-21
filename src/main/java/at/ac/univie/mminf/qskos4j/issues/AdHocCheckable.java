package at.ac.univie.mminf.qskos4j.issues;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;

public interface AdHocCheckable {

    public abstract void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException;

}
