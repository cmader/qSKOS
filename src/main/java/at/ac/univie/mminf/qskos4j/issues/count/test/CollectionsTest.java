package at.ac.univie.mminf.qskos4j.issues.count.test;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.count.AggregationRelations;
import at.ac.univie.mminf.qskos4j.issues.count.Collections;
import at.ac.univie.mminf.qskos4j.issues.count.SemanticRelations;
import at.ac.univie.mminf.qskos4j.result.general.NumberResult;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.query.TupleQueryResult;

import java.io.IOException;

/**
 * Created by christian
 * Date: 26.01.13
 * Time: 14:47
 */
public class CollectionsTest extends IssueTestCase {

    private Collections collections;

    @Before
    public void setUp() throws OpenRDFException, IOException {
        collections = (Collections) setUpIssue("aggregations.rdf", new Collections());
    }

    @Test
    public void testAggregationRelationsCount() throws OpenRDFException
    {
        Assert.assertEquals(4, collections.getResult().getData().longValue());
    }

}
