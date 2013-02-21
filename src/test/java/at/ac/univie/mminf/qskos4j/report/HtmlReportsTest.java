package at.ac.univie.mminf.qskos4j.report;

import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.DisjointLabelsViolations;
import at.ac.univie.mminf.qskos4j.issues.labels.util.ResourceLabelsCollector;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

public class HtmlReportsTest {

    private DisjointLabelsViolations disjointLabelsViolations;
    private DisconnectedConceptClusters disconnectedConceptClusters;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        disjointLabelsViolations = new DisjointLabelsViolations(
            new ResourceLabelsCollector(VocabRepository.setUpFromTestResource("ambiguousLabels.rdf").getRepository()));
        disconnectedConceptClusters = new DisconnectedConceptClusters(
            new InvolvedConcepts(VocabRepository.setUpFromTestResource("components.rdf")));

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

            String htmlReport = htmlStringWriter.toString();
            System.out.println(htmlReport);

            providesReport &= (htmlStringWriter.toString().trim().length() != 0);
        }

        return providesReport;
    }

}
