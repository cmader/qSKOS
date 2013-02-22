package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.pp.adhoc.RelationClashesAdHoc;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;

public class RelationClashesAdHocTest {

    private RelationClashesAdHoc relationClashesAdHocTest;
    private RepositoryConnection repCon;

    @Before
    public void setUp() throws IOException, OpenRDFException
    {
        repCon = new RepositoryBuilder().setUpFromTestResource("relationClashesAdHoc.rdf").getConnection();
        relationClashesAdHocTest = new RelationClashesAdHoc(repCon);
    }

    @After
    public void tearDown() throws RepositoryException
    {
        repCon.close();
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelClash_broader() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptA"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/conceptB")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelClash_broaderTransitive() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptD"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/conceptC")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelClash_broadMatch() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptE"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "relatedMatch"),
                        new URIImpl("http://myvocab.org/conceptF")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelClash_narrower() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptG"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/conceptH")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelClash_related() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptI"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "narrower"),
                        new URIImpl("http://myvocab.org/conceptJ")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceFirstLevelClash_relatedMatch() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptK"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broaderTransitive"),
                        new URIImpl("http://myvocab.org/conceptL")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceSecondLevelClash_broader() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptM"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/conceptO")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceSecondLevelClash_related() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptR"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "narrower"),
                        new URIImpl("http://myvocab.org/conceptP")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceSecondLevelClash_mixedBroaderTransitive() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptU"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/conceptS")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceSecondLevelClash_mixedBroaderNarrower() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptV"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/conceptW")
                )
        );
    }

    @Test(expected = IssueOccursException.class)
    public void introduceSecondLevelClash_mixedRelated() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/concept1"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "broader"),
                        new URIImpl("http://myvocab.org/concept3")
                )
        );
    }

    @Test
    public void introduceNoClash() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/concept4"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "related"),
                        new URIImpl("http://myvocab.org/concept6")
                )
        );
    }

    @Test
    public void introduceNonHierarchicalOrAssociativeRelation() throws IssueOccursException, OpenRDFException
    {
        relationClashesAdHocTest.checkStatement(
                new StatementImpl(
                        new URIImpl("http://myvocab.org/conceptA"),
                        new URIImpl(SparqlPrefix.SKOS.getNameSpace() + "scopeNote"),
                        new URIImpl("http://myvocab.org/conceptB")
                )
        );
    }


}
