package at.ac.univie.mminf.qskos4j;

import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openrdf.model.URI;

public class ConceptRankTest extends QSkosTestCase {

	@Test
	public void testConceptRank() {
		qSkosRankConcepts.addSparqlEndPoint("http://sparql.sindice.com/sparql");
		
		qSkosRankConcepts.setPublishingHost("dbpedia.org");
		Map<URI, Set<URI>> conceptRank = qSkosRankConcepts.analyzeConceptsRank(null);
		for (URI concept : conceptRank.keySet()) {
			if (concept.stringValue().equals("http://dbpedia.org/resource/Michael_Jackson")) 
			{
				Assert.assertEquals(31, conceptRank.get(concept).size());
			}
		}
	}
	
}
