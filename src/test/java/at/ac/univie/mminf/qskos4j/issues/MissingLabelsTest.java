package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.conceptscheme.ConceptSchemes;
import at.ac.univie.mminf.qskos4j.issues.labels.MissingLabels;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;

import java.io.IOException;
import java.util.Collection;

public class MissingLabelsTest {

    private MissingLabels missingLabels;
    private Collection<Resource> conceptsAndConceptSchemesWithMissingLabels;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        RepositoryConnection repCon = new RepositoryBuilder().setUpFromTestResource("missingLabels.rdf").getConnection();

        AuthoritativeConcepts authConcepts = new AuthoritativeConcepts(new InvolvedConcepts());
        ConceptSchemes conceptSchemes = new ConceptSchemes();
        missingLabels = new MissingLabels(authConcepts, conceptSchemes);
        missingLabels.setRepositoryConnection(repCon);
    }

    @Test
    public void checkLabeledConcepts() throws OpenRDFException {
        conceptsAndConceptSchemesWithMissingLabels = missingLabels.getResult().getData();

        Assert.assertTrue(isUnlabeled("conceptA"));
        Assert.assertTrue(isUnlabeled("conceptC"));
        Assert.assertTrue(isUnlabeled("conceptD"));
        Assert.assertTrue(isUnlabeled("conceptE"));
        Assert.assertTrue(isUnlabeled("conceptG"));

        Assert.assertFalse(isUnlabeled("conceptB"));
        Assert.assertFalse(isUnlabeled("conceptF"));
        Assert.assertFalse(isUnlabeled("conceptH"));
        Assert.assertFalse(isUnlabeled("conceptI"));
    }

    @Test
    public void checkLabeledConceptSchemes() throws OpenRDFException {
        conceptsAndConceptSchemesWithMissingLabels = missingLabels.getResult().getData();

        Assert.assertTrue(isUnlabeled("conceptSchemeC"));
        Assert.assertTrue(isUnlabeled("conceptSchemeD"));

        Assert.assertFalse(isUnlabeled("conceptSchemeA"));
        Assert.assertFalse(isUnlabeled("conceptSchemeB"));
        Assert.assertFalse(isUnlabeled("conceptSchemeE"));
        Assert.assertFalse(isUnlabeled("conceptSchemeF"));
    }

    @Test
    public void countMissingLabels() throws OpenRDFException {
        conceptsAndConceptSchemesWithMissingLabels = missingLabels.getResult().getData();
        Assert.assertEquals(7, conceptsAndConceptSchemesWithMissingLabels.size());
    }

    private boolean isUnlabeled(String uriSuffix) {
        for (Value unlabeledResource : conceptsAndConceptSchemesWithMissingLabels) {
            if (unlabeledResource.stringValue().endsWith(uriSuffix))
                return true;
        }
        return false;
    }

}
