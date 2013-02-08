package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.report.Report;
import ch.qos.logback.classic.Logger;
import org.openrdf.OpenRDFException;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

class ReportCollector {

    private Logger logger = (Logger) LoggerFactory.getLogger(ReportCollector.class);

    private Collection<Issue> issues;

    public ReportCollector(Collection<Issue> issues) {
        this.issues = issues;
    }

    void outputURITrackingReport(File uriTrackFile) throws IOException
    {
        Map<String, Collection<String>> conceptIssuesMapping = collectIssuesForConcepts(uriTrackFile);

        outputCsvHeader();
        outputIssuesAsCsv(conceptIssuesMapping);
    }

    private void outputCsvHeader() {
        System.out.print("Concept;");
        for (Issue issue : issues) {
            System.out.print(issue.getId() + ";");
        }
        System.out.println();
    }

    private void outputIssuesAsCsv(Map<String, Collection<String>> conceptIssuesMapping) {
        for (Map.Entry<String, Collection<String>> entry : conceptIssuesMapping.entrySet()) {
            System.out.print(entry.getKey() + ";");

            for (Issue issue : issues) {
                for (String issueForConcept : entry.getValue()) {
                    if (issueForConcept.contains(issue.getId())) {
                        System.out.print(issueForConcept);
                    }
                }
                System.out.print(";");
            }
            System.out.println();
        }
    }

    public Map<String, Collection<String>> collectIssuesForConcepts(File uriTrackFile) throws IOException
    {
        Map<String, Collection<String>> uriSubstringToIssuesMapping = new LinkedHashMap<String, Collection<String>>();

        for (Issue issue : issues) {
            ConceptIterator conceptIterator = new ConceptIterator(uriTrackFile);

            try {
                StringWriter extensiveTextReportStringWriter = new StringWriter();
                BufferedWriter extensiveTextReportStringBufWriter = new BufferedWriter(extensiveTextReportStringWriter);
                issue.getResult().generateReport(extensiveTextReportStringBufWriter, Report.ReportFormat.TXT, Report.ReportStyle.EXTENSIVE);
                extensiveTextReportStringBufWriter.close();

                addConceptOccurences(uriSubstringToIssuesMapping, issue, extensiveTextReportStringWriter.toString(), conceptIterator);
            }
            catch (OpenRDFException e) {
                logger.error("Error invoking measure " +issue.getName()+ " (" +issue.getId()+ ")");
            }
        }

        return uriSubstringToIssuesMapping;
    }

    private void addConceptOccurences(
        Map<String, Collection<String>> uriSubstringToIssuesMapping,
        Issue issue,
        String report,
        ConceptIterator conceptIterator)
    {
        while (conceptIterator.hasNext()) {
            String conceptSubString = conceptIterator.next();

            Collection<String> containingMeasures = uriSubstringToIssuesMapping.get(conceptSubString);
            if (containingMeasures == null) {
                containingMeasures = new HashSet<String>();
                uriSubstringToIssuesMapping.put(conceptSubString, containingMeasures);
            }

            int conceptPosInReport = report.indexOf(conceptSubString);
            if (conceptPosInReport != -1) {
                StringBuilder measureId = new StringBuilder(issue.getId());

                if (issue instanceof DisconnectedConceptClusters) {
                    measureId.append("(").append(getWccSize(conceptPosInReport, report)).append(")");
                }

                containingMeasures.add(measureId.toString());
            }
        }
    }

    private String getWccSize(int conceptPosInReport, String report) {
        String WCC_SIZE_DESC = "size: ";

        int sizeDescPos = report.lastIndexOf(WCC_SIZE_DESC, conceptPosInReport);
        int newLinePos = report.indexOf("\n", sizeDescPos);

        return report.substring(sizeDescPos + WCC_SIZE_DESC.length(), newLinePos);
    }

    void outputIssuesReport(boolean outputExtendedReport, boolean shouldWriteGraphs)
    {
        for (Issue issue : issues) {
            System.out.println("--- " +issue.getName());

            try {
                StringWriter stringWriter = new StringWriter();
                BufferedWriter reportStringWriter = new BufferedWriter(stringWriter);
                issue.getResult().generateReport(reportStringWriter, Report.ReportFormat.TXT, Report.ReportStyle.SHORT);

                if (outputExtendedReport) {
                    reportStringWriter.newLine();
                    issue.getResult().generateReport(reportStringWriter, Report.ReportFormat.TXT, Report.ReportStyle.EXTENSIVE);
                }

                reportStringWriter.close();
                System.out.println(stringWriter.toString());

                if (shouldWriteGraphs) {
                    BufferedWriter graphFileWriter = new BufferedWriter(new FileWriter(issue.getId() + ".dot"));
                    issue.getResult().generateReport(graphFileWriter, Report.ReportFormat.DOT);
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
