package at.ac.univie.mminf.qskos4j.issues.skosintegrity;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.util.Collection;

public class HierarchicalRedundancy extends Issue<Collection<Pair<Resource>>> {

    public HierarchicalRedundancy() {
        super("hr",
            "Hierarchical Redundancy",
            "Finds broader/narrower relations over multiple hierarchy levels",
            IssueType.ANALYTICAL);
    }

    @Override
    protected Collection<Pair<Resource>> computeResult() throws OpenRDFException {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private String createQuery() {
        return SparqlPrefix.SKOS + "SELECT * WHERE {" +
            "?concept skos:broader ?otherConcept . " +
            "?concept skos:broader{2,} ?otherConcept" +
        "}";
    }

    @Override
    protected Report generateReport(Collection<Pair<Resource>> preparedData) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
