package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.RelatedConcepts;

public class RelatedConceptsTest extends QSkosTestCase {

	private QSkos qSkosComponents, qSkosRelatedConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosComponents = setUpInstance("components.rdf");
		qSkosRelatedConcepts = setUpInstance("relatedConcepts.rdf");
	}
	
	@Test
	public void testRelatedConceptsCount_1() {
		Set<RelatedConcepts> allRelatedConcepts = qSkosComponents.findRelatedConcepts();
		
		Assert.assertEquals(2, allRelatedConcepts.size());
		Assert.assertEquals(4, getDifferentConcepts(allRelatedConcepts).size());
	}

	@Test
	public void testNotDirectlyConnectedRelatedConceptsCount() {
		Set<RelatedConcepts> allRelatedConcepts = qSkosComponents.findRelatedConcepts();
		
		long notDirectlyConnectedConceptsCount = 0;
		for (RelatedConcepts relatedConcepts : allRelatedConcepts) {
			if (!relatedConcepts.getDirectlyConnected()) {
				notDirectlyConnectedConceptsCount++;
			}
		}
		
		Assert.assertEquals(1, notDirectlyConnectedConceptsCount);
	}
	
	private Set<URI> getDifferentConcepts(Set<RelatedConcepts> allRelatedConcepts) 
	{
		Set<URI> ret = new HashSet<URI>();
		
		for (RelatedConcepts relatedConcepts : allRelatedConcepts) {
			ret.add(relatedConcepts.getConcept1());
			ret.add(relatedConcepts.getConcept2());
		}
		
		return ret;
	}
	
	@Test
	public void testRelatedConceptsCount_2() {
		Assert.assertEquals(0, qSkosRelatedConcepts.findRelatedConcepts().size());
	}
}
