package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.pp.LabelMatch;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;

public class LabelMatchTest {

    private LabelMatch labelLengthMatch;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        labelLengthMatch = new LabelMatch(VocabRepository.setUpFromTestResource("labelMatch.rdf"), "^.{1,2}$");
    }

    @Test
    public void testMaxLabelLength() throws OpenRDFException
    {
        Assert.assertEquals(4, labelLengthMatch.getReport().getData().size());
    }

}
