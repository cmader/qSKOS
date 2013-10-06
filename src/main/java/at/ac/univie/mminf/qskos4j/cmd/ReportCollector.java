package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import org.openrdf.OpenRDFException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

class ReportCollector {

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

        for (Issue issue : issues) {
            writeTextReport(issue, reportWriter, outputExtendedReport);

            if (shouldWriteGraphs) {
                writeGraphFiles(issue, getDotFilesPath(reportFile));
            }
        }

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
        writer.write("--- " +issue.getName());
        writer.newLine();
        issue.getReport().generateReport(writer, Report.ReportFormat.TXT, Report.ReportStyle.SHORT);

        if (outputExtendedReport) {
            writer.newLine();
            issue.getReport().generateReport(writer, Report.ReportFormat.TXT, Report.ReportStyle.EXTENSIVE);
        }

        writer.newLine();
        writer.flush();
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
