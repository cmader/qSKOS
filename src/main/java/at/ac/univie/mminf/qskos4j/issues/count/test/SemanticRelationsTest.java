package at.ac.univie.mminf.qskos4j.issues.count.test;

import at.ac.univie.mminf.qskos4j.issues.count.SemanticRelations;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:22
 */
public class SemanticRelationsTest extends IssueTestCase {

    private SemanticRelations semanticRelations;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        semanticRelations = new SemanticRelations(setUpRepository("components.rdf"));
    }

    @Test
    public void testLexicalRelationsCount() throws OpenRDFException
    {
        Assert.assertEquals(18, semanticRelations.getResult().getData().longValue());
    }

}
