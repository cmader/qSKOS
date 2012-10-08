package at.ac.univie.mminf.qskos4j.cmd;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.MeasureDescription;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.MeasureInvoker;
import at.ac.univie.mminf.qskos4j.util.measureinvocation.QSKOSMethodInvocationException;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ReportGenerator {

    private Logger logger = (Logger) LoggerFactory.getLogger(ReportGenerator.class);

    private Set<MeasureDescription> measures;
    private MeasureInvoker invoker;

    public ReportGenerator(MeasureInvoker invoker, Set<MeasureDescription> measures) {
        this.measures = measures;
        this.invoker = invoker;
    }

    void outputURITrackingReport(File uriTrackFile) throws IOException
    {
        Map<String, Collection<String>> conceptIssuesMapping = collectIssuesForConcepts(uriTrackFile);

        outputCsvHeader();
        outputIssuesAsCsv(conceptIssuesMapping);
    }

    private void outputCsvHeader() {
        System.out.print("Concept;");
        for (MeasureDescription measure : measures) {
            System.out.print(measure.getId() + ";");
        }
        System.out.println();
    }

    private void outputIssuesAsCsv(Map<String, Collection<String>> conceptIssuesMapping) {
        for (String conceptSubstring : conceptIssuesMapping.keySet()) {
            Collection<String> issues = conceptIssuesMapping.get(conceptSubstring);

            System.out.print(conceptSubstring + ";");
            for (MeasureDescription measure : measures) {
                for (String issue : issues) {
                    if (issue.contains(measure.getId())) {
                        System.out.print(issue);
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

        for (MeasureDescription measure : measures) {
            ConceptIterator conceptIterator = new ConceptIterator(uriTrackFile);

            try {
                Result<?> result = invoker.getMeasureResult(measure);

                String report = result.getExtensiveReport();
                addConceptOccurences(uriSubstringToIssuesMapping, measure, report, conceptIterator);
            }
            catch (QSKOSMethodInvocationException e) {
                logger.error("Error invoking measure " +measure.getName()+ " (" +measure.getId()+ ")");
            }
        }

        return uriSubstringToIssuesMapping;
    }

    private void addConceptOccurences(
        Map<String, Collection<String>> uriSubstringToIssuesMapping,
        MeasureDescription measure,
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
                String measureId = measure.getId();

                if (measure == MeasureDescription.WEAKLY_CONNECTED_COMPONENTS) {
                    measureId += "(" +getWccSize(conceptPosInReport, report)+ ")";
                }

                containingMeasures.add(measureId);
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
        for (MeasureDescription measure : measures) {
            System.out.println("--- " +measure.getName());

            try {
                Result<?> result = invoker.getMeasureResult(measure);
                System.out.println(result.getShortReport());

                if (outputExtendedReport) {
                    System.out.println(result.getExtensiveReport());
                }

                if (shouldWriteGraphs) {
                    try {
                        writeGraphsToFiles(result.getAsDOT(), measure.getId());
                    }
                    catch (IOException e) {
                        logger.error("error writing graph file for issue " +measure.getId(), e);
                    }

                }

            }
            catch (QSKOSMethodInvocationException e) {
                logger.error("Error getting measure result", e);
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
