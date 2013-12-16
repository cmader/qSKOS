package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.relations.UnidirectionallyRelatedConcepts;
import at.ac.univie.mminf.qskos4j.util.Tuple;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.io.IOException;
import java.util.Map;

public class UnidirectionallyRelatedConceptsTest {

    private UnidirectionallyRelatedConcepts unidirectionallyRelatedConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        unidirectionallyRelatedConcepts = new UnidirectionallyRelatedConcepts(new AuthoritativeConcepts(new InvolvedConcepts()));
        unidirectionallyRelatedConcepts.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource("omittedInverseRelations.rdf").getConnection());
    }

    @Test
    public void testMissingInverseRelationsCount() throws OpenRDFException {
        Map<Tuple<Resource>, String> missingRelations = unidirectionallyRelatedConcepts.getResult();
        Assert.assertEquals(8, missingRelations.size());
    }
}
