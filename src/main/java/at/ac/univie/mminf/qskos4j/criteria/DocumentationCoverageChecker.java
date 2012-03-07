package at.ac.univie.mminf.qskos4j.criteria;

import java.util.Collection;
import java.util.Iterator;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;

import at.ac.univie.mminf.qskos4j.result.general.NumberResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class DocumentationCoverageChecker extends Criterion {

	private String[] documentationProperties = {
		"skos:note", "skos:changeNote", "skos:definition", "skos:editorialNote",
		"skos:example", "skos:historyNote", "skos:scopeNote"
	};
	
	public DocumentationCoverageChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public NumberResult<Float> getAverageDocumentationCoverageRatio(
		Collection<URI> authoritativeConcepts) throws OpenRDFException
	{
		float docCoverage = 0;
		
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(authoritativeConcepts, progressMonitor);
		while (conceptIt.hasNext()) {
			docCoverage += (float) findDocPropertyCount(conceptIt.next()) / 
						   (float) documentationProperties.length;
		}
		
		return new NumberResult<Float>(docCoverage / authoritativeConcepts.size());
	}
	
	private int findDocPropertyCount(URI concept) 
		throws OpenRDFException 
	{
		int docPropertyCount = 0;
		
		for (String docProperty : documentationProperties) {
			if (conceptHasProperty(concept, docProperty)) {
				docPropertyCount++;
			}
		}
		
		return docPropertyCount;
	}
	
	private boolean conceptHasProperty(URI concept, String property) 
		throws OpenRDFException
	{
		RepositoryConnection connection = vocabRepository.getRepository().getConnection();
		BooleanQuery graphQuery = connection.prepareBooleanQuery(
			QueryLanguage.SPARQL, 
			createPropertyQuery(concept, property));
		return graphQuery.evaluate();
	}
	
	private String createPropertyQuery(URI concept, String property) {
		return SparqlPrefix.SKOS + "ASK {<"+concept.stringValue()+"> " +property+ "?o}";
	}

}
