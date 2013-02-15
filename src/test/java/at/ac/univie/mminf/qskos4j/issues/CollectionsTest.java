package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.count.Collections;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
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
public class CollectionsTest {

    private Collections collections;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        collections = new Collections(VocabRepository.setUpFromTestResource("aggregations.rdf"));
    }

    @Test
    public void testCollectionsCount() throws OpenRDFException
    {
        Assert.assertEquals(4, collections.getReport().getData().longValue());
    }

}
