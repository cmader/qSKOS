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
        for (String conceptSubstring : conceptIssuesMapping.keySet()) {
            Collection<String> issues = conceptIssuesMapping.get(conceptSubstring);

            if (!issues.isEmpty()) {
                System.out.println("Concept (containing) '" +conceptSubstring+ "':");
                System.out.println("\tOccurs in issue(s): " + issues.toString());
            }
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

            if (report.contains(conceptSubString)) {
                containingMeasures.add(measure.getId());
            }
        }
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
