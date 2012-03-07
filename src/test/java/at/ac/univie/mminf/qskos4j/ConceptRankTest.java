package at.ac.univie.mminf.qskos4j;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;

public class ConceptRankTest extends QSkosTestCase {

	private QSkos qSkosRankConcepts;
	
	@Before
	public void setUp() throws OpenRDFException, IOException {
		qSkosRankConcepts = setUpInstance("rankConcepts.rdf");
	}
	
	@Test
	public void testConceptRank() throws OpenRDFException {
		qSkosRankConcepts.addSparqlEndPoint("http://sparql.sindice.com/sparql");
		
		qSkosRankConcepts.setPublishingHost("dbpedia.org");
		Map<URI, Set<URI>> conceptRank = qSkosRankConcepts.analyzeConceptsRank().getData();
		for (URI concept : conceptRank.keySet()) {
			if (concept.stringValue().equals("http://dbpedia.org/resource/Michael_Jackson")) 
			{
				Assert.assertEquals(31, conceptRank.get(concept).size());
			}
		}
	}
	
}
