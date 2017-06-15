package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.count.SemanticRelations;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:22
 */
public class SemanticRelationsTest {

    private SemanticRelations semanticRelations;

    @Before
    public void setUp() throws RDF4JException, IOException {
        semanticRelations = new SemanticRelations();
        semanticRelations.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection());
    }

    @Test
    public void testLexicalRelationsCount() throws RDF4JException
    {
        Assert.assertEquals(18, semanticRelations.getResult().getData().longValue());
    }

}
