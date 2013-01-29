package at.ac.univie.mminf.qskos4j.issues.labels.test;

import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.labels.OverlappingLabels;
import at.ac.univie.mminf.qskos4j.issues.labels.util.LabelConflict;
import at.ac.univie.mminf.qskos4j.util.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OverlappingLabelsTest extends IssueTestCase {

    private OverlappingLabels overlappingLabelsForComponents, overlappingLabelsForRelatedConcepts;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        overlappingLabelsForComponents = new OverlappingLabels(new InvolvedConcepts(setUpRepository("components.rdf")));
        overlappingLabelsForRelatedConcepts = new OverlappingLabels(new InvolvedConcepts(setUpRepository("relatedConcepts.rdf")));
    }

    @Test
    public void testLabelConflictCount_1() throws OpenRDFException {
        Collection<LabelConflict> allLabelConflicts = overlappingLabelsForComponents.getResult().getData();

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
        Assert.assertEquals(0, overlappingLabelsForRelatedConcepts.getResult().getData());
    }
}
