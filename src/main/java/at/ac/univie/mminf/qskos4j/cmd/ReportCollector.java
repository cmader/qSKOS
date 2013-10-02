package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

class ReportCollector {

    private Logger logger = (Logger) LoggerFactory.getLogger(ReportCollector.class);

    private Collection<Issue> issues;

    public ReportCollector(Collection<Issue> issues) {
        this.issues = issues;
    }

    void outputIssuesReport(boolean outputExtendedReport, boolean shouldWriteGraphs)
    {
        for (Issue issue : issues) {
            System.out.println("--- " +issue.getName());

            try {
                StringWriter stringWriter = new StringWriter();
                BufferedWriter reportStringWriter = new BufferedWriter(stringWriter);
                issue.getReport().generateReport(reportStringWriter, Report.ReportFormat.TXT, Report.ReportStyle.SHORT);

                if (outputExtendedReport) {
                    reportStringWriter.newLine();
                    issue.getReport().generateReport(reportStringWriter, Report.ReportFormat.TXT, Report.ReportStyle.EXTENSIVE);
                }

                reportStringWriter.close();
                System.out.println(stringWriter.toString());

                if (shouldWriteGraphs) {
                    BufferedWriter graphFileWriter = new BufferedWriter(new FileWriter(issue.getId() + ".dot"));
                    issue.getReport().generateReport(graphFileWriter, Report.ReportFormat.DOT);
                    graphFileWriter.close();
                }
            }
            catch (OpenRDFException rdfEx) {
                logger.error("Error getting issue report", rdfEx);
            }
            catch (IOException ioEx) {
                logger.error("Error generating report output", ioEx);
            }
        }
    }

}
