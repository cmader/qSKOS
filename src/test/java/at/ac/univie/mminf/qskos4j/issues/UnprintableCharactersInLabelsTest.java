package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.UnprintableCharactersInLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabeledConcept;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.eclipse.rdf4j.RDF4JException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;

public class UnprintableCharactersInLabelsTest {

    private UnprintableCharactersInLabels unprintableCharactersInLabels;

    @Before
    public void setUp() throws RDF4JException, IOException {
        unprintableCharactersInLabels = new UnprintableCharactersInLabels(new AuthoritativeConcepts(new InvolvedConcepts()));
        unprintableCharactersInLabels.setRepositoryConnection(new RepositoryBuilder().setUpFromTestResource(
            "labelsWithUnprintableCharacters.rdf").getConnection());
    }

    @Test
    public void labelsWithUnprintableCharacters_count() throws RDF4JException {
        Collection<LabeledConcept> invalidLabels = unprintableCharactersInLabels.getResult().getData();

        Assert.assertEquals(2, invalidLabels.size());
    }

}
