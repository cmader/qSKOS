package at.ac.univie.mminf.qskos4j.issues.relations.test;

import at.ac.univie.mminf.qskos4j.issues.relations.ValuelessAssociativeRelations;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;

public class ValuelessAssociativeRelationsTest extends IssueTestCase {

    private ValuelessAssociativeRelations valuelessAssociativeRelations;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        valuelessAssociativeRelations = new ValuelessAssociativeRelations(setUpRepository("redundantAssociativeRelations.rdf"));
    }

    @Test
    public void testRedundantAssociativeRelationsCount() throws OpenRDFException {
        Collection<Pair<URI>> redAssRels = valuelessAssociativeRelations.getResult().getData();
        Assert.assertEquals(6, redAssRels.size());
    }
}
