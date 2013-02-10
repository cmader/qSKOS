package at.ac.univie.mminf.qskos4j.servlet;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report.ReportFormat;
import at.ac.univie.mminf.qskos4j.report.Report.ReportStyle;
import at.ac.univie.mminf.qskos4j.report.UnsupportedReportFormatException;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class QualityIssueServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(QualityIssueServlet.class);

    private final static String REQ_PARAM_ISSUE_ID = "issueId";
    private final static String REQ_PARAM_REPORT_FORMAT = "reportFormat";
    private final static String REQ_PARAM_REPORT_STYLE = "reportStyle";

    private QSkos qskos;
    private Collection<Issue> requestedIssues;
    private ReportFormat requestedReportFormat;
    private ReportStyle requestedReportStyle;

    @Override
    public void init() throws ServletException {
        Repository repository = (Repository) getServletContext().getAttribute("repository");

        try {
            qskos = new QSkos(new VocabRepository(repository));
        }
        catch (Exception e) {
            throw new UnavailableException("Error creating qSKOS instance");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req);
        generateResponse(resp);
    }

    private void processRequest(HttpServletRequest req) {
        String issueIds = req.getParameter(REQ_PARAM_ISSUE_ID);

        requestedIssues = qskos.getIssues(issueIds);
        requestedReportFormat = getReportFormat(req.getParameter(REQ_PARAM_REPORT_FORMAT));
        requestedReportStyle = getReportStyle(req.getParameter(REQ_PARAM_REPORT_STYLE));
    }

    private ReportFormat getReportFormat(String requestedReportFormat) {
        try {
            return ReportFormat.valueOf(requestedReportFormat);
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedReportFormatException("Report format must be one of " +Arrays.asList(ReportFormat.values()).toString());
        }
        catch (NullPointerException e) {
            return ReportFormat.TXT;
        }
    }

    private ReportStyle getReportStyle(String requestedReportStyle) {
        try {
            return ReportStyle.valueOf(requestedReportStyle);
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedReportStyleException("Report style must by one of " +Arrays.asList(ReportStyle.values()).toString());
        }
        catch (NullPointerException e) {
            return ReportStyle.SHORT;
        }
    }

    private void generateResponse(HttpServletResponse resp) throws IOException
    {
        BufferedWriter responseWriter = new BufferedWriter(resp.getWriter());
        generateReports(responseWriter);
        responseWriter.close();
        resp.setContentType(inferContentType());
        resp.setHeader("Cache-Control", "no-cache");
    }

    private void generateReports(BufferedWriter responseWriter) throws IOException
    {
        for (Issue issue : requestedIssues) {
            try {
                issue.getReport().generateReport(responseWriter, requestedReportFormat, requestedReportStyle);
            }
            catch (OpenRDFException e) {
                logger.error("Error generating report for issue id: '" +issue.getId()+ "'", e);
            }
        }
    }

    private String inferContentType() {
        switch (requestedReportFormat) {
            case TXT:
                return "text/plain";

            case RDF:
                return "application/rdf+xml";

            case DOT:
                return "text/vnd.graphviz";

            case PDF:
                return "application/pdf";

            default:
                // should never happen
                return "application/octet-stream";
        }
    }
}
