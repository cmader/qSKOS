package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.clusters.DisconnectedConceptClusters;
import at.ac.univie.mminf.qskos4j.result.Result;
import ch.qos.logback.classic.Logger;
import org.openrdf.OpenRDFException;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

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
                BufferedWriter extensiveTextReportStringWriter = new BufferedWriter(new StringWriter());
                issue.getResult().generateReport(extensiveTextReportStringWriter, Result.ReportFormat.TXT, Result.ReportStyle.EXTENSIVE);
                extensiveTextReportStringWriter.close();

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
        BufferedWriter reportStringWriter = new BufferedWriter(new StringWriter());

        for (Issue issue : issues) {
            System.out.println("--- " +issue.getName());

            try {
                issue.getResult().generateReport(reportStringWriter, Result.ReportFormat.TXT, Result.ReportStyle.SHORT);

                if (outputExtendedReport) {
                    issue.getResult().generateReport(reportStringWriter, Result.ReportFormat.TXT, Result.ReportStyle.EXTENSIVE);
                }

                reportStringWriter.close();
                System.out.println(reportStringWriter.toString());

                if (shouldWriteGraphs) {
                    try {
                        writeGraphsToFiles(result.getAsDOT(), issue.getId());
                    }
                    catch (IOException e) {
                        logger.error("error writing graph file for issue " +issue.getId(), e);
                    }

                }
            }
            catch (OpenRDFException rdfEx) {
                logger.error("Error getting measure result", rdfEx);
            }
            catch (IOException ioEx) {
                logger.error("Error generating report output", ioEx);
            }
        }
    }

    private void writeGraphsToFiles(Collection<String> dotGraphs, String fileName)
            throws IOException
    {
        int i = 0;
        Iterator<String> it = dotGraphs.iterator();
        while (it.hasNext()) {
            String dotGraph = it.next();
            FileWriter writer = new FileWriter(new File(fileName +"_"+ i +".dot"));
            writer.write(dotGraph);
            writer.close();
            i++;
        }
    }

}
