package at.ac.univie.mminf.qskos4j.issues.conceptscheme;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;

import java.util.Collection;

public class MappingRelationsMisuse extends Issue<Collection<Statement>> {

    public MappingRelationsMisuse(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
            "mri",
            "Mapping Relations Misuse",
            "Finds concepts within the same concept scheme that are related by a mapping relation",
            IssueType.ANALYTICAL);
    }

    @Override
    protected Collection<Statement> computeResult() throws OpenRDFException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Report generateReport(Collection<Statement> preparedData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
