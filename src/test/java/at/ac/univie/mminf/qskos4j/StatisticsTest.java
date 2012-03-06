package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;

public class StatisticsTest extends QSkosTestCase {
	
	private QSkos qSkosConcepts, qSkosComponents, qSkosAggregations;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosConcepts = setUpInstance("concepts.rdf");
		qSkosComponents = setUpInstance("components.rdf");
		qSkosAggregations = setUpInstance("aggregations.rdf");
	}
	
	@Test
	public void testTripleCount_1() throws RepositoryException {
		long tripleCount = qSkosConcepts.getTripleCount();
		Assert.assertEquals(20, tripleCount);		
	}
	
	@Test
	public void testConceptCount_1() throws OpenRDFException
	{
		Set<URI> involvedConcepts = qSkosConcepts.getInvolvedConcepts();
		Assert.assertEquals(10, involvedConcepts.size());
	}


	@Test
	public void testTripleCount_2() throws RepositoryException {
		long tripleCount = qSkosComponents.getTripleCount();
		Assert.assertEquals(73, tripleCount);
	}
	
	@Test
	public void testConceptsCount_2() throws OpenRDFException
	{
		Set<URI> involvedConcepts = qSkosComponents.getInvolvedConcepts(); 
		Assert.assertEquals(21, involvedConcepts.size());		
	}
	
	@Test 
	public void testAuthoritativeConceptsCount() throws OpenRDFException
	{
		qSkosConcepts.setPublishingHost("zbw.eu");
		Set<URI> authoritativeConcepts = qSkosConcepts.getAuthoritativeConcepts();
		Assert.assertEquals(9, authoritativeConcepts.size());
	}
	
	@Test
	public void testLexicalRelationsCount() throws OpenRDFException
	{
		Assert.assertEquals(29, qSkosComponents.findLexicalRelationsCount());
	}
	
	@Test
	public void testSemanticRelationsCount() throws OpenRDFException
	{
		Assert.assertEquals(18, qSkosComponents.findSemanticRelationsCount());
	}
	
	@Test
	public void testAggregationRelationsCount() throws OpenRDFException
	{
		Assert.assertEquals(6, qSkosAggregations.findAggregationRelations());
	}
	
	@Test
	public void testConceptSchemeCount() throws OpenRDFException
	{
		Assert.assertEquals(5, qSkosAggregations.findConceptSchemeCount());
	}
	
}
