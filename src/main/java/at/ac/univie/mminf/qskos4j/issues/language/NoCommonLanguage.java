package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.OpenRDFException;

public class NoCommonLanguage extends Issue {

    public NoCommonLanguage() {
        super("ncl",
              "No Common Language",
              "Checks if concept labels are available in at least one common language",
              IssueType.ANALYTICAL);
    }

    @Override
    protected Result<?> invoke() throws OpenRDFException {
        return null;
    }

}
