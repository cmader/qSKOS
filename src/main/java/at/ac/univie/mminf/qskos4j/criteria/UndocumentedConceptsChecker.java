package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class UndocumentedConceptsChecker extends Criterion {

	private String[] documentationProperties = {
		"skos:note", "skos:changeNote", "skos:definition", "skos:editorialNote",
		"skos:example", "skos:historyNote", "skos:scopeNote"
	};
	
	public UndocumentedConceptsChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public CollectionResult<Resource> findUndocumentedConcepts(Collection<URI> allConcepts) 
		throws OpenRDFException
	{
		List<Resource> undocumentedConcepts = new ArrayList<Resource>();
		
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(allConcepts, progressMonitor);
		while (conceptIt.hasNext()) {
			Resource concept = conceptIt.next();
			if (!isConceptDocumented(concept)) {
				undocumentedConcepts.add(concept);
			}
		}
		
		return new CollectionResult<Resource>(undocumentedConcepts);
	}
	
	private boolean isConceptDocumented(Resource concept) 
		throws OpenRDFException 
	{		
		for (String docProperty : documentationProperties) {
			if (conceptHasProperty(concept, docProperty)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean conceptHasProperty(Resource concept, String property) 
		throws OpenRDFException
	{
		RepositoryConnection connection = vocabRepository.getRepository().getConnection();
		BooleanQuery graphQuery = connection.prepareBooleanQuery(
			QueryLanguage.SPARQL, 
			createPropertyQuery(concept, property));
		return graphQuery.evaluate();
	}
	
	private String createPropertyQuery(Resource concept, String property) {
		return SparqlPrefix.SKOS + "ASK {<"+concept.stringValue()+"> " +property+ "?o}";
	}

}
