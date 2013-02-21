package at.ac.univie.mminf.qskos4j.issues.pp.adhoc;

import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;

interface AdHocCheckable {

    public abstract void checkStatement(Statement statement) throws IssueOccursException, OpenRDFException;

}
