package at.ac.univie.mminf.qskos4j.servlet;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QualityIssueServlet extends HttpServlet {

    private QSkos qskos;

    @Override
    public void init() throws ServletException {
        Repository repository = (Repository) getServletContext().getAttribute("repository");

        try {
            qskos = new QSkos(new VocabRepository(repository));
        }
        catch (Exception e) {
            throw new ServletException("Error creating qSKOS instance", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
