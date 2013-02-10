package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.relations.ValuelessAssociativeRelations;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;

public class ValuelessAssociativeRelationsTest {

    private ValuelessAssociativeRelations valuelessAssociativeRelations;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        valuelessAssociativeRelations = new ValuelessAssociativeRelations(VocabRepository.setUpFromTestResource("redundantAssociativeRelations.rdf"));
    }

    @Test
    public void testRedundantAssociativeRelationsCount() throws OpenRDFException {
        Collection<Pair<URI>> redAssRels = valuelessAssociativeRelations.getReport().getData();
        Assert.assertEquals(6, redAssRels.size());
    }
}
