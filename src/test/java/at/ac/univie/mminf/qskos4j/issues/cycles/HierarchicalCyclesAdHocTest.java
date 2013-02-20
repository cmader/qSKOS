package at.ac.univie.mminf.qskos4j.issues.cycles;

import at.ac.univie.mminf.qskos4j.issues.HierarchyGraphBuilder;
import at.ac.univie.mminf.qskos4j.issues.IssueOccursException;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;

import java.io.IOException;

/**
 * This class tests "ad-hoc" addition of statements to a repository that may cause cylces in the hierarchical
 * structure.
 */
public class HierarchicalCyclesAdHocTest {

    private HierarchicalCycles hierarchicalCycles;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        VocabRepository cyclesAdHocRepo = VocabRepository.setUpFromTestResource("cyclesAdHoc.rdf");
        hierarchicalCycles = new HierarchicalCycles(new HierarchyGraphBuilder(cyclesAdHocRepo));
    }

    @Test
    public void initiallyNoCycle() throws OpenRDFException
    {
        Assert.assertEquals(0, hierarchicalCycles.getReport().getData().size());
    }

    @Test
    public void statementChecksDontChangeRepository() throws OpenRDFException {
        RepositoryConnection repCon = hierarchicalCycles.getVocabRepository().getRepository().getConnection();
        long sizeBeforeCheck = repCon.size();

        try {
            introduceReflexiveCycle();

            // the method above MUST throw an exception, so we should not arrive here
            Assert.fail();
        }
        catch (IssueOccursException e) {
            long sizeAfterCheck = repCon.size();
            Assert.assertTrue(sizeBeforeCheck == sizeAfterCheck);
        }
        finally {
            repCon.close();
        }
    }

    @Test
    public void statementChecksDontChangeHierarchyGraph() throws OpenRDFException {
        // do initial cycle finding
        hierarchicalCycles.getReport();

        int[] graphSizeBeforeAdHocCheck = hierarchicalCycles.getHierarchyGraphSize();
        try {
            introduceReflexiveCycle();

            // the method above MUST throw an exception, so we should not arrive here
            Assert.fail();
        }
        catch (IssueOccursException e) {
            int[] graphSizeAfterAdHocCheck = hierarchicalCycles.getHierarchyGraphSize();
            boolean sameVertexCount = graphSizeBeforeAdHocCheck[0] == graphSizeAfterAdHocCheck[0];
            boolean sameEdgeCount = graphSizeBeforeAdHocCheck[1] == graphSizeAfterAdHocCheck[1];

            Assert.assertTrue(sameVertexCount && sameEdgeCount);
        }
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

}
