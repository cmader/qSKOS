package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.count.SemanticRelations;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
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
public class SemanticRelationsTest {

    private SemanticRelations semanticRelations;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        semanticRelations = new SemanticRelations(VocabRepository.setUpFromTestResource("components.rdf"));
    }

    @Test
    public void testLexicalRelationsCount() throws OpenRDFException
    {
        Assert.assertEquals(18, semanticRelations.getReport().getData().longValue());
    }

}
