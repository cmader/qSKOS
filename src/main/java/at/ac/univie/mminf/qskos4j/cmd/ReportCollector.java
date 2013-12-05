package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.Result;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    void outputIssuesReport(boolean shouldWriteGraphs)
        throws IOException, OpenRDFException
    {
        File reportFile = createReportFile();
        BufferedWriter reportWriter = new BufferedWriter(new FileWriter(reportFile));

        processIssues();
        writeReportHeader(reportWriter, reportFile);
        writeReportSummary(reportWriter);
        writeReportBody(reportWriter, reportFile, shouldWriteGraphs);

        reportWriter.close();
    }

    private void writeReportHeader(BufferedWriter reportWriter,
                                   File reportFile) throws IOException {
        String issuedDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(new Date());
        String fileName = reportFile.getAbsolutePath();
        reportWriter.write("This is the quality report of file " +fileName+ ", generated on " +issuedDate);
        reportWriter.newLine();
        reportWriter.newLine();

    }

    private void processIssues() throws OpenRDFException {
        int issueNumber = 0;
        Iterator<Issue> issueIt = issues.iterator();
        while (issueIt.hasNext()) {
            Issue issue = issueIt.next();
            issueNumber++;

            logger.info("Processing issue " + issueNumber + " of " + issues.size() + " (" + issue.getName() + ")");
            issue.getResult();

        }

        logger.info("Result complete!");


    }

    private void writeReportSummary(BufferedWriter reportWriter) throws IOException, OpenRDFException {
        reportWriter.write("Summary of Quality Issues:\n");

        for (Issue issue : issues) {
            issue.getResult().indicatesProblem();


            StringWriter stringWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter(stringWriter);
            issue.getResult().generateReport(
                    writer,
                    Result.ReportFormat.TXT,
                    Result.ReportStyle.SHORT);
            writer.close();
            reportWriter.write(issue.getName() + ": " +stringWriter.toString()+ "\n");
        }

        reportWriter.newLine();
    }

    private void writeReportBody(BufferedWriter reportWriter,
                                 File reportFile,
                                 boolean shouldWriteGraphs)
        throws IOException, OpenRDFException
    {
        Iterator<Issue> issueIt = issues.iterator();
        while (issueIt.hasNext()) {
            Issue issue = issueIt.next();

            writeTextReport(issue, reportWriter);

            if (issueIt.hasNext()) {
                reportWriter.newLine();
            }

            if (shouldWriteGraphs) {
                writeGraphFiles(issue, getDotFilesPath(reportFile));
            }
        }
    }

    private File createReportFile() throws IOException {
        File file = new File(reportFileName);
        file.createNewFile();
        return file;
    }

    private void writeTextReport(Issue issue, BufferedWriter writer)
        throws IOException, OpenRDFException
    {
        writer.write(createIssueHeader(issue));
        writer.newLine();
        issue.getResult().generateReport(writer, Result.ReportFormat.TXT, Result.ReportStyle.SHORT);

        writer.newLine();
        issue.getResult().generateReport(writer, Result.ReportFormat.TXT, Result.ReportStyle.EXTENSIVE);

        writer.newLine();
        writer.flush();
    }

    private String createIssueHeader(Issue issue) {
        String header = "--- " +issue.getName();
        URI weblink = issue.getWeblink();
        header += "\nDescription: " +issue.getDescription();

        if (weblink != null) {
            header += "\nDetailed information: " +weblink.stringValue();
        }
        return header;
    }

    private String getDotFilesPath(File reportFile) {
        String absolutePath = reportFile.getAbsolutePath();
        return absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
    }

    private void writeGraphFiles(Issue issue, String dotFilesPath) throws IOException, OpenRDFException {
        BufferedWriter graphFileWriter = new BufferedWriter(new FileWriter(dotFilesPath + issue.getId() + ".dot"));
        issue.getResult().generateReport(graphFileWriter, Result.ReportFormat.DOT);
        graphFileWriter.close();
    }

}
