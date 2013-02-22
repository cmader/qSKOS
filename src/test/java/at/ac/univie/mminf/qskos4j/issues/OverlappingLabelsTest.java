package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OverlappingLabelsTest {

    private OverlappingLabels overlappingLabelsForComponents, overlappingLabelsForRelatedConcepts;
    private RepositoryConnection componentsRepCon, relatedConceptsRepCon;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        componentsRepCon = new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection();
        relatedConceptsRepCon = new RepositoryBuilder().setUpFromTestResource("relatedConcepts.rdf").getConnection();

        overlappingLabelsForComponents = new OverlappingLabels(new InvolvedConcepts(componentsRepCon));
        overlappingLabelsForRelatedConcepts = new OverlappingLabels(new InvolvedConcepts(relatedConceptsRepCon));
    }

    @After
    public void tearDown() throws RepositoryException
    {
        componentsRepCon.close();
        relatedConceptsRepCon.close();
    }

    @Test
    public void testLabelConflictCount_1() throws OpenRDFException {
        Collection<LabelConflict> allLabelConflicts = overlappingLabelsForComponents.getReport().getData();

        Assert.assertEquals(2, allLabelConflicts.size());
        Assert.assertEquals(4, getDifferentResources(allLabelConflicts).size());
    }

    private Collection<Value> getDifferentResources(Collection<LabelConflict> labelConflicts)
    {
        Set<Value> ret = new HashSet<Value>();

        for (LabelConflict labelConflict : labelConflicts) {
            ret.addAll(labelConflict.getAffectedResources());
        }

        return ret;
    }

    @Test
    public void testLabelConflictCount_2() throws OpenRDFException {
        Assert.assertEquals(0, overlappingLabelsForRelatedConcepts.getReport().getData().size());
    }
}
