package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MissingOutLinks extends Issue {

    private final Logger logger = LoggerFactory.getLogger(MissingOutLinks.class);

    private String authResourceIdentifier;
    private Collection<URI> autoritativeConcepts;
	private Map<URI, Collection<URL>> extResourcesForConcept;
	
	public MissingOutLinks() {
		super("mol", "Missing Out-Links", "Finds concepts that are not linked to other vocabularies on the Web");
	}

    public void setAuthResourceIdentifier(String authResourceIdentifier) {
        this.authResourceIdentifier = authResourceIdentifier;
    }

    public void setAutoritativeConcepts(Collection<URI> autoritativeConcepts) {
        this.autoritativeConcepts = autoritativeConcepts;
    }

    @Override
    public Result<?> invoke() {
		extResourcesForConcept = new HashMap<URI, Collection<URL>>();
		this.authResourceIdentifier = authResourceIdentifier;
		
		findResourcesForConcepts(autoritativeConcepts);
		
		return new CollectionResult<URI>(extractUnlinkedConcepts());
	}
	
	private void findResourcesForConcepts(Collection<URI> concepts)
	{
		Iterator<URI> conceptIt = new MonitoredIterator<URI>(concepts, progressMonitor, "finding resources");
		
		while (conceptIt.hasNext()) {
			URI concept = conceptIt.next();			
			extResourcesForConcept.put(concept, findExternalResourcesForConcept(concept));
		}
	}
	
	private Collection<URL> findExternalResourcesForConcept(URI concept) 
	{
        List<URL> resourceList = new ArrayList<URL>();
		String query = createIRIQuery(concept); 

        try {
            TupleQueryResult result = vocabRepository.query(query);
            resourceList = identifyResources(result);
        }
        catch (OpenRDFException e) {
            logger.error("Error finding references to/from concept '" +concept+ "'");
        }
		
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
                // ignore exception
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
