package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.issues.pp.adhoc.HierarchicalCyclesAdHoc;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;

/**
 * This class tests "ad-hoc" addition of statements to a repository that may cause cylces in the hierarchical
 * structure.
 */
public class HierarchicalCyclesAdHocTest {

    private HierarchicalCyclesAdHoc hierarchicalCycles;
    private RepositoryConnection repCon;

    @Before
    public void setUp() throws OpenRDFException, IOException
    {
        repCon = new RepositoryBuilder().setUpFromTestResource("cyclesAdHoc.rdf").getConnection();
        hierarchicalCycles = new HierarchicalCyclesAdHoc(repCon);
    }

    @After
    public void tearDown() throws RepositoryException
    {
        repCon.close();
    }

    @Test(expected = IssueOccursException.class)
    public void introduceReflexiveCycle() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptA"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broader"),
                        new URIImpl("http://myvocab.org/conceptA")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelCycle_broader() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptB"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broader"),
                        new URIImpl("http://myvocab.org/conceptA")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelCycle_narrower() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptA"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "narrower"),
                        new URIImpl("http://myvocab.org/conceptB")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceMultiLevelCycle() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptC"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broader"),
                        new URIImpl("http://myvocab.org/conceptA")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceTransitiveCycle_broader() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptF"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broaderTransitive"),
                        new URIImpl("http://myvocab.org/conceptD")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceTransitiveCycle_narrower() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptD"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "narrowerTransitive"),
                        new URIImpl("http://myvocab.org/conceptE")
                )
        );
    }

    @Test
    public void introduceNoCycle() throws IssueOccursException, OpenRDFException {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptA"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broader"),
                        new URIImpl("http://myvocab.org/conceptX")
                )
        );

        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptC"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "narrower"),
                        new URIImpl("http://myvocab.org/conceptY")
                )
        );

        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptZ"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broaderTransitive"),
                        new URIImpl("http://myvocab.org/conceptV")
                )
        );
    }

    @Test
    public void testNonHierarchicalStatement() throws IssueOccursException, OpenRDFException
    {
        hierarchicalCycles.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptA"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "scopeNote"),
                        new LiteralImpl("some scope note")
                )
        );
    }

}
