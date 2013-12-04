package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;


class ReportCollector {

    private final Logger logger = LoggerFactory.getLogger(ReportCollector.class);

    private Collection<Issue> issues;
    private String reportFileName;

    public ReportCollector(Collection<Issue> issues, String reportFileName) {
        this.issues = issues;
        this.reportFileName = reportFileName;
    }

    void outputIssuesReport(boolean outputExtendedReport, boolean shouldWriteGraphs)
        throws IOException, OpenRDFException
    {
        File reportFile = createReportFile();
        BufferedWriter reportWriter = new BufferedWriter(new FileWriter(reportFile));

        writeReportHeader(reportWriter, reportFile);
        writeReportBody(reportWriter, reportFile, outputExtendedReport, shouldWriteGraphs);
    }

    private void writeReportHeader(BufferedWriter reportWriter,
                                   File reportFile) throws IOException {
        String issuedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date());
        String fileName = reportFile.getAbsolutePath();
        reportWriter.write("This is the quality report of file " +fileName+ ", generated on " +issuedDate);
        reportWriter.newLine();
        reportWriter.newLine();
    }

    private void writeReportBody(BufferedWriter reportWriter,
                                 File reportFile,
                                 boolean outputExtendedReport,
                                 boolean shouldWriteGraphs)
        throws IOException, OpenRDFException
    {
        int issueNumber = 0;
        Iterator<Issue> issueIt = issues.iterator();
        while (issueIt.hasNext()) {
            Issue issue = issueIt.next();
            issueNumber++;

            logger.info("Processing issue " +issueNumber+ " of " +issues.size()+ " (" +issue.getName()+ ")");
            writeTextReport(issue, reportWriter, outputExtendedReport);
            if (issueIt.hasNext()) {
                reportWriter.newLine();
            }

            if (shouldWriteGraphs) {
                writeGraphFiles(issue, getDotFilesPath(reportFile));
            }
        }

        logger.info("Report complete!");
        reportWriter.close();
    }

    private File createReportFile() throws IOException {
        File file = new File(reportFileName);
        file.createNewFile();
        return file;
    }

    private void writeTextReport(Issue issue, BufferedWriter writer, boolean outputExtendedReport)
        throws IOException, OpenRDFException
    {
        writer.write(createIssueHeader(issue));
        writer.newLine();
        issue.getReport().generateReport(writer, Report.ReportFormat.TXT, Report.ReportStyle.SHORT);

        if (outputExtendedReport) {
            writer.newLine();
            issue.getReport().generateReport(writer, Report.ReportFormat.TXT, Report.ReportStyle.EXTENSIVE);
        }

        writer.newLine();
        writer.flush();
    }

    private String createIssueHeader(Issue issue) {
        String header = "--- " +issue.getName();
        URI weblink = issue.getWeblink();
        if (weblink != null) {
            header += ", cf. <" +weblink.stringValue()+ ">";
        }
        return header;
    }

    private String getDotFilesPath(File reportFile) {
        String absolutePath = reportFile.getAbsolutePath();
        return absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
    }

    private void writeGraphFiles(Issue issue, String dotFilesPath) throws IOException, OpenRDFException {
        BufferedWriter graphFileWriter = new BufferedWriter(new FileWriter(dotFilesPath + issue.getId() + ".dot"));
        issue.getReport().generateReport(graphFileWriter, Report.ReportFormat.DOT);
        graphFileWriter.close();
    }

}
