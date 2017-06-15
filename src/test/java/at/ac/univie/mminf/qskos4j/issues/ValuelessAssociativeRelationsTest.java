package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.relations.ValuelessAssociativeRelations;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.URI;

import java.io.IOException;
import java.util.Collection;

public class ValuelessAssociativeRelationsTest {

    private ValuelessAssociativeRelations valuelessAssociativeRelations;

    @Before
    public void setUp() throws RDF4JException, IOException {
        valuelessAssociativeRelations = new ValuelessAssociativeRelations();
        valuelessAssociativeRelations.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("redundantAssociativeRelations.rdf").getConnection());
    }

    @Test
    public void testRedundantAssociativeRelationsCount() throws RDF4JException {
        Collection<Tuple<URI>> redAssRels = valuelessAssociativeRelations.getResult().getData();
        Assert.assertEquals(6, redAssRels.size());
    }
}
