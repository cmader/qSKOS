package at.ac.univie.mminf.qskos4j.servlet;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.report.UnsupportedReportFormatException;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.repository.Repository;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class QualityIssueServlet extends HttpServlet {

    private final static String REQ_PARAM_ISSUE_ID = "issueId";
    private final static String REQ_PARAM_REPORT_FORMAT = "reportFormat";
    private final static String REQ_PARAM_REPORT_STYLE = "reportStyle";

    private QSkos qskos;

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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req);
    }

    private void processRequest(HttpServletRequest req) {
        String issueIds = req.getParameter(REQ_PARAM_ISSUE_ID);
        Collection<Issue> issues = qskos.getIssues(issueIds);


        checkReportFormat(req.getParameter(REQ_PARAM_REPORT_FORMAT));
        checkReportStyle(req.getParameter(REQ_PARAM_REPORT_STYLE));

        ;
        throw new UnsupportedReportFormatException("blabla");
    }

    private void checkReportFormat(String reportFormat) {
        throw new UnsupportedReportFormatException("");
    }

    private void checkReportStyle(String reportStyle) {
        throw new UnsupportedReportStyleException("");
    }
}
