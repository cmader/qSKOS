package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;

public class HierarchicalCyclesLoadTest {

    private static HierarchicalCycles hierarchicalCycles;
    private static RepositoryConnection repCon;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();

    @BeforeClass
    public static void setUp() throws OpenRDFException, IOException {
        VocabRepository repo = VocabRepository.setUpFromTestResource("stw.rdf");
        //VocabRepository repo = VocabRepository.setUpFromTestResource("cycles.rdf");

        hierarchicalCycles = new HierarchicalCycles(new HierarchyGraphBuilder(repo));
        repCon = repo.getRepository().getConnection();
    }

    @AfterClass
    public static void tearDown() throws RepositoryException
    {
        repCon.close();
    }

    @Test
    public void checkAndAddStatment() throws OpenRDFException
    {
        try {
            Statement someStatement = createRandomStatement();
            hierarchicalCycles.checkStatement(someStatement);
            repCon.add(someStatement);
        }
        catch (IssueOccursException e) {
            // will never be thrown with these created statments
            Assert.fail();
        }
    }

    private Statement createRandomStatement() {
        String vocabNamespace = "http://myvocab.org/";
        return new StatementImpl(
            new URIImpl(vocabNamespace + System.currentTimeMillis()),
            new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broader"),
            new URIImpl(vocabNamespace + System.currentTimeMillis()));
    }
}
