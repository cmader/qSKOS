package at.ac.univie.mminf.qskos4j.issues.count.test;

import at.ac.univie.mminf.qskos4j.issues.count.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.issues.count.SemanticRelations;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:47
 */
public class ConceptSchemesTest extends IssueTestCase {

    private ConceptSchemes conceptSchemes;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        conceptSchemes = (ConceptSchemes) setUpIssue("aggregations.rdf", new ConceptSchemes());
    }

    @Test
    public void testLexicalRelationsCount() throws OpenRDFException
    {
        Assert.assertEquals(5, conceptSchemes.getResult().getData().size());
    }
}
