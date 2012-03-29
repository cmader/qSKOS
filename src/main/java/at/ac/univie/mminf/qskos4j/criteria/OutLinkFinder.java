package at.ac.univie.mminf.qskos4j.criteria;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class OutLinkFinder extends Criterion {

	private String authResourceIdentifier;
	private Map<URI, Collection<URL>> extResourcesForConcept;
	
	public OutLinkFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public CollectionResult<URI> findMissingOutLinks(
		Collection<URI> autoritativeConcepts,
		String authResourceIdentifier) throws OpenRDFException 
	{
		extResourcesForConcept = new HashMap<URI, Collection<URL>>();
		this.authResourceIdentifier = authResourceIdentifier;
		
		findResourcesForConcepts(autoritativeConcepts);
		
		return new CollectionResult<URI>(extractUnlinkedConcepts());
	}
	
	private void findResourcesForConcepts(Collection<URI> concepts) throws OpenRDFException 
	{
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(concepts, progressMonitor, "finding resources");
		
		while (conceptIt.hasNext()) {
			URI concept = conceptIt.next();			
			extResourcesForConcept.put(concept, findExternalResourcesForConcept(concept));
		}
	}
	
	private Collection<URL> findExternalResourcesForConcept(URI concept) 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		String query = createIRIQuery(concept); 
		
		TupleQueryResult result = vocabRepository.query(query);
		List<URL> resourceList = identifyResources(result);
		
		return extractExternalResources(resourceList);
	}
	
	private String createIRIQuery(URI concept) {
		return "SELECT DISTINCT ?iri "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+
				"WHERE {{<"+concept.stringValue()+"> ?p ?iri .} UNION "+
					"{?iri ?p <"+concept.stringValue()+"> .}"+
					"FILTER isIRI(?iri) "+
					"FILTER regex(str(?iri), \"^http\")}";
	}
		
	private List<URL> identifyResources(TupleQueryResult iriTuples) 
		throws QueryEvaluationException 
	{
		List<URL> ret = new ArrayList<URL>();
		
		while (iriTuples.hasNext()) {
			Value iri = iriTuples.next().getValue("iri");
			
			try {
				URL url = new URL(iri.stringValue());
				ret.add(url);
			} 
			catch (MalformedURLException e) {
				continue;
			}			
		}
		
		return ret;
	}
	
	private Collection<URL> extractExternalResources(Collection<URL> allResources) {
		Collection<URL> validExternalResources = new HashSet<URL>();
		
		for (URL url : allResources) {
			if (isExternalResource(url) && isNonSkosURL(url)) {
				validExternalResources.add(url);
			}
		}
		
		return validExternalResources;
	}
	
	private boolean isExternalResource(URL url) {
		if (authResourceIdentifier != null && !authResourceIdentifier.isEmpty()) {
			return !url.toString().toLowerCase().contains(authResourceIdentifier.toLowerCase());
		}
		
		throw new IllegalArgumentException("external resource identifier must not be null or empty");
	}
	
	private boolean isNonSkosURL(URL url) {
		return !url.toString().contains(SparqlPrefix.SKOS.getNameSpace());
	}
	
	private Collection<URI> extractUnlinkedConcepts() {
		Collection<URI> unlinkedConcepts = new HashSet<URI>();
		
		for (URI concept : extResourcesForConcept.keySet()) {
			if (extResourcesForConcept.get(concept).isEmpty()) {
				unlinkedConcepts.add(concept);
			}
		}
		
		return unlinkedConcepts;
	}
	
}
