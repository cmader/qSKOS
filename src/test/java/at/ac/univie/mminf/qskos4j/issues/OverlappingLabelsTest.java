package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.util.vocab.RepositoryBuilder;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryConnection;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OverlappingLabelsTest {

    private OverlappingLabels overlappingLabelsForComponents, overlappingLabelsForRelatedConcepts, overlappingLabels;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        RepositoryConnection componentsRepCon = new RepositoryBuilder().setUpFromTestResource("components.rdf").getConnection();
        RepositoryConnection relatedConceptsRepCon = new RepositoryBuilder().setUpFromTestResource("relatedConcepts.rdf").getConnection();
        RepositoryConnection overlappingLabelsRepCon = new RepositoryBuilder().setUpFromTestResource("overlappingLabels.rdf").getConnection();

        overlappingLabelsForComponents = new OverlappingLabels(new InvolvedConcepts(componentsRepCon));
        overlappingLabelsForRelatedConcepts = new OverlappingLabels(new InvolvedConcepts(relatedConceptsRepCon));
        overlappingLabels = new OverlappingLabels(new InvolvedConcepts(overlappingLabelsRepCon));
    }

    @Test
    public void testLabelConflictCount_1() throws OpenRDFException {
        Collection<LabelConflict> allLabelConflicts = overlappingLabelsForComponents.getPreparedData();

        Assert.assertEquals(2, allLabelConflicts.size());
        Assert.assertEquals(4, getDifferentResources(allLabelConflicts).size());
    }

    @Test
    public void testCaseInsensitive() throws OpenRDFException {
        Assert.assertEquals(2, overlappingLabels.getPreparedData().size());
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
        Assert.assertEquals(0, overlappingLabelsForRelatedConcepts.getPreparedData().size());
    }
}
