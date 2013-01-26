package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.QSkos;
import at.ac.univie.mminf.qskos4j.util.test.IssueTestCase;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import java.io.IOException;
import java.util.Collection;


public class StatisticsTest extends IssueTestCase {
	
	private QSkos qSkosConcepts, qSkosComponents, qSkosAggregations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpIssue("concepts.rdf");
		qSkosComponents = setUpIssue("components.rdf");
		qSkosAggregations = setUpIssue("aggregations.rdf");
	}
	

	@Test
	public void testSemanticRelationsCount() throws OpenRDFException
	{
		Assert.assertEquals(18, qSkosComponents.findSemanticRelationsCount().getData().longValue());
	}
	
	@Test
	public void testAggregationRelationsCount() throws OpenRDFException
	{
		Assert.assertEquals(7, qSkosAggregations.findAggregationRelations().getData().longValue());
	}
	
	@Test
	public void testConceptSchemeCount() throws OpenRDFException
	{
		Assert.assertEquals(5, qSkosAggregations.findConceptSchemes().getData().size());
	}
	
	@Test
	public void testCollectionCount() throws OpenRDFException
	{
		Assert.assertEquals(4, qSkosAggregations.findCollectionCount().getData().longValue());
	}
	
}
