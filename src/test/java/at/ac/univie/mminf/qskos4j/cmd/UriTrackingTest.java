package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.language.IncompleteLanguageCoverage;
import at.ac.univie.mminf.qskos4j.util.QskosTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class UriTrackingTest extends QskosTestCase {

    private ReportCollector reportCollector;
    private File uriTrackFile;
    private Issue disconnectedConceptClusters, incompleteLanguageCoverage;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        InvolvedConcepts involvedConcepts = new InvolvedConcepts(setUpRepository("components.rdf"));

        disconnectedConceptClusters = new DisconnectedConceptClusters(involvedConcepts);
        incompleteLanguageCoverage = new IncompleteLanguageCoverage(involvedConcepts);
        Collection<Issue> issues = new ArrayList<Issue>();
        issues.add(disconnectedConceptClusters);
        issues.add(incompleteLanguageCoverage);

        reportCollector = new ReportCollector(issues);

        URL uriTrackFileUrl = getClass().getResource("/uriTrackFile.txt");
        uriTrackFile = new File(uriTrackFileUrl.getFile());
    }

    @Test
    public void testUriTracking() {
        try {
            Map<String, Collection<String>> issuesForConcepts = reportCollector.collectIssuesForConcepts(uriTrackFile);

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
                if (issue.contains(disconnectedConceptClusters.getId())) {
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
                if (issue.contains(incompleteLanguageCoverage.getId())) {
                    noUncoveredLanguages = false;
                }
            }

            if (noUncoveredLanguages) conceptsWithNoUncoveredLanguages++;
        }

        return conceptsWithNoUncoveredLanguages == 1;
    }

}
