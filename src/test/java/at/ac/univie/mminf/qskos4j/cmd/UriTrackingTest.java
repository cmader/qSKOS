package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.MeasureInvoker;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UriTrackingTest extends IssueTestCase {

    private QSkos qSkosComponents;
    private ReportGenerator reportGenerator;
    private File uriTrackFile;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        qSkosComponents = setUpRepository("components.rdf");

        Set<MeasureDescription> measures = new HashSet<MeasureDescription>();

        measures.add(MeasureDescription.DISCONNECTED_CONCEPT_CLUSTERS);
        measures.add(MeasureDescription.INCOMPLETE_LANG_COVERAGE);

        reportGenerator = new ReportGenerator(
            new MeasureInvoker(qSkosComponents),
            measures);

        URL uriTrackFileUrl = getClass().getResource("/uriTrackFile.txt");
        uriTrackFile = new File(uriTrackFileUrl.getFile());
    }

    @Test
    public void testUriTracking() {
        try {
            Map<String, Collection<String>> issuesForConcepts = reportGenerator.collectIssuesForConcepts(uriTrackFile);

            Assert.assertTrue(allConceptsAreInOneCluster(issuesForConcepts));
            Assert.assertTrue(oneConceptHasNoUncoveredLanguages(issuesForConcepts));
        }
        catch (IOException e) {
            Assert.fail();
        }
    }

    private boolean allConceptsAreInOneCluster(Map<String, Collection<String>> issuesForConcepts) {
        boolean wccIssueFound;

        for (Collection<String> issuesOfConcept : issuesForConcepts.values()) {
            wccIssueFound = false;

            for (String issue : issuesOfConcept) {
                if (issue.contains(MeasureDescription.DISCONNECTED_CONCEPT_CLUSTERS.getId())) {
                    wccIssueFound = true;
                }
            }

            if (!wccIssueFound) return false;
        }

        return true;
    }

    private boolean oneConceptHasNoUncoveredLanguages(Map<String, Collection<String>> issuesForConcepts) {
        int conceptsWithNoUncoveredLanguages = 0;

        for (String concept : issuesForConcepts.keySet()) {
            boolean noUncoveredLanguages = true;

            for (String issue : issuesForConcepts.get(concept)) {
                if (issue.contains(MeasureDescription.INCOMPLETE_LANG_COVERAGE.getId())) {
                    noUncoveredLanguages = false;
                }
            }

            if (noUncoveredLanguages) conceptsWithNoUncoveredLanguages++;
        }

        return conceptsWithNoUncoveredLanguages == 1;
    }

}
