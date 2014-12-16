package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.UnprintableCharactersInLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;

import java.io.IOException;
import java.util.Collection;

public class UnprintableCharactersInLabelsTest {

    private UnprintableCharactersInLabels unprintableCharactersInLabels;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        unprintableCharactersInLabels = new UnprintableCharactersInLabels(new AuthoritativeConcepts(new InvolvedConcepts()));
        unprintableCharactersInLabels.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource(
            "labelsWithUnprintableCharacters.rdf").getConnection());
    }

    @Test
    public void labelsWithUnprintableCharacters_count() throws OpenRDFException {
        Collection<LabeledConcept> invalidLabels = unprintableCharactersInLabels.getResult().getData();

        Assert.assertEquals(1, invalidLabels.size());
    }

}
