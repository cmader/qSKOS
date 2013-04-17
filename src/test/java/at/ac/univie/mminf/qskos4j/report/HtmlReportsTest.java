package at.ac.univie.mminf.qskos4j.report;

import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.DisjointLabelsViolations;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class HtmlReportsTest {

    private DisjointLabelsViolations disjointLabelsViolations;
    private DisconnectedConceptClusters disconnectedConceptClusters;
    private RepositoryConnection disjointLabelsRepCon, clustersRepCon;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        disjointLabelsViolations = new DisjointLabelsViolations(new ResourceLabelsCollector());
        disjointLabelsViolations.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("ambiguousLabels.rdf").getConnection());

        disconnectedConceptClusters = new DisconnectedConceptClusters(new InvolvedConcepts());
        disconnectedConceptClusters.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @After
    public void tearDown() throws RepositoryException
    {
        disjointLabelsRepCon.close();
        clustersRepCon.close();
    }

    @Test
    public void issuesProvideHtmlReport() throws OpenRDFException, IOException {
        Assert.assertTrue(providesHtmlReport(disjointLabelsViolations.getReport()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void issuesDontProvideHtmlReport() throws OpenRDFException, IOException {
        providesHtmlReport(disconnectedConceptClusters.getReport());
    }

    private boolean providesHtmlReport(Report report) throws IOException {
        boolean providesReport = true;

        for (Report.ReportStyle style : Arrays.asList(Report.ReportStyle.values())) {
            StringWriter htmlStringWriter = new StringWriter();
            BufferedWriter htmlReportWriter = new BufferedWriter(htmlStringWriter);

            report.generateHtmlReport(htmlReportWriter, style);
            htmlReportWriter.close();

            providesReport &= (htmlStringWriter.toString().trim().length() != 0);
        }

        return providesReport;
    }

}
