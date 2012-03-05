package at.ac.univie.mminf.qskos4j.criteria;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class ExternalResourcesFinder extends Criterion {

	private final Logger logger = LoggerFactory.getLogger(ExternalResourcesFinder.class);
	
	private String publishingHost;
	private Map<URI, List<URL>> resourcesForConcept;
	
	public ExternalResourcesFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public Map<URI, List<URL>> findExternalResourcesForConcepts(
		Set<URI> concepts,
		String publishingHost) throws OpenRDFException 
	{
		resourcesForConcept = new HashMap<URI, List<URL>>();
		this.publishingHost = publishingHost;
		
		findResourcesForConcepts(concepts);
		if (publishingHost == null) {
			guessPublishingHost();
		}
		retainExternalResources();
		
		return resourcesForConcept;
	}
	
	private void findResourcesForConcepts(Set<URI> concepts) throws OpenRDFException 
	{
		Iterator<URI> conceptIt = getMonitoredIterator("finding resources", concepts);
		
		while (conceptIt.hasNext()) {
			URI concept = conceptIt.next();			
			resourcesForConcept.put(concept, findResourcesForConcept(concept));
		}
	}
	
	private List<URL> findResourcesForConcept(URI concept) 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		String query = createIRIQuery(concept); 
		
		TupleQueryResult result = queryRepository(query);
		List<URL> resourceList = identifyResources(result);
		
		return resourceList;
	}
	
	private String createIRIQuery(URI concept) {
		return "SELECT DISTINCT ?iri "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+
				"WHERE {{<"+concept.stringValue()+"> ?p ?iri .} UNION "+
					"{?iri ?p <"+concept.stringValue()+"> .}"+
					"FILTER isIRI(?iri) "+
					"FILTER regex(str(?iri), \"^http\")}";
	}
		
	private void guessPublishingHost() throws QueryEvaluationException {
		HostNameOccurrencies hostNameOccurencies = new HostNameOccurrencies();
		
		Iterator<List<URL>> resourcesListIt = getMonitoredIterator(
			"guessing publishing host", 
			resourcesForConcept.values());
		while (resourcesListIt.hasNext()) {
			for (URL conceptResource : resourcesListIt.next()) {
				hostNameOccurencies.put(conceptResource.getHost());
			}
		}
		
		publishingHost = hostNameOccurencies.getMostOftenOccuringHostName();
		logger.info("Guessed publishing host: '" +publishingHost+ "'");
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
	
	private void retainExternalResources() {
		List<URL> validExternalResources = new ArrayList<URL>();
		
		Iterator<URI> conceptIt = getMonitoredIterator(
			"retaining external resources", 
			resourcesForConcept.keySet());
		while (conceptIt.hasNext()) {
			URI concept = conceptIt.next();
			
			List<URL> resourceURLs = resourcesForConcept.get(concept);

			for (URL url : resourceURLs) {
				if (publishingHostMismatch(url) && isNonSkosURL(url)) {
					validExternalResources.add(url);
				}
			}
			
			resourceURLs.retainAll(validExternalResources);
		}
	}
	
	private boolean publishingHostMismatch(URL url) {
		return !url.getHost().equals(publishingHost);
	}
	
	private boolean isNonSkosURL(URL url) {
		return !url.toString().contains(SparqlPrefix.SKOS.getNameSpace());
	}
	
	@SuppressWarnings("serial")
	private class HostNameOccurrencies extends HashMap<String, Integer>
	{
		HostNameOccurrencies() {
			super();
		}
		
		void put(String hostname) {
			Integer occurencies = get(hostname);
			put(hostname, occurencies == null ? 1 : ++occurencies);
		}
		
		String getMostOftenOccuringHostName() {
			SortedSet<Map.Entry<String, Integer>> sortedEntries = new TreeSet<Map.Entry<String, Integer>>(
				new Comparator<Map.Entry<String, Integer>>() 
				{
					@Override 
					public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
						return e2.getValue().compareTo(e1.getValue());
					}
				}
			);
			
			sortedEntries.addAll(entrySet());
			return sortedEntries.first().getKey();
		}
	}
}
