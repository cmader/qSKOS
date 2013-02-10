package at.ac.univie.mminf.qskos4j.servlet;

import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 10.02.13
 * Time: 13:37
 */
public class ServerStarter {

    private Server server;

    public static void main(String[] args) {
        new ServerStarter();
    }

    private ServerStarter() {
        try {
            setUp();
            server.start();
        }
        catch (Exception e) {
            System.out.println("Error setting up quality checking server; " + e.toString());
        }
    }

    private void setUp() throws IOException, OpenRDFException {
        server = new Server(8080);
        Context root = new Context(server,"/",Context.SESSIONS);
        root.setAttribute("repository", VocabRepository.setUpFromTestResource("testvocab.rdf").getRepository());
        root.addServlet(new ServletHolder(new QualityIssueServlet()), "/*");
    }

}
