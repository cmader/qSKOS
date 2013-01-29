package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Finds concepts without links to "external" resources (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_OutLinks">Missing Out-Links</a>.
 */
public class MissingOutLinks extends Issue<CollectionResult<Value>> {

    private final Logger logger = LoggerFactory.getLogger(MissingOutLinks.class);

	private Map<Value, Collection<URL>> extResourcesForConcept;
    private AuthoritativeConcepts authoritativeConcepts;
	
	public MissingOutLinks(AuthoritativeConcepts authoritativeConcepts) {
		super(authoritativeConcepts.getVocabRepository(),
              "mol",
              "Missing Out-Links",
              "Finds concepts that are not linked to other vocabularies on the Web",
              IssueType.ANALYTICAL
        );

        this.authoritativeConcepts = authoritativeConcepts;
	}

    @Override
    public CollectionResult<Value> invoke() throws OpenRDFException {
		extResourcesForConcept = new HashMap<Value, Collection<URL>>();

		findResourcesForConcepts(authoritativeConcepts.getResult().getData());
		
		return new CollectionResult<Value>(extractUnlinkedConcepts());
	}
	
	private void findResourcesForConcepts(Collection<Value> concepts)
	{
		Iterator<Value> conceptIt = new MonitoredIterator<Value>(concepts, progressMonitor, "finding resources");
		
		while (conceptIt.hasNext()) {
            Value concept = conceptIt.next();
			extResourcesForConcept.put(concept, findExternalResourcesForConcept(concept));
		}
	}
	
	private Collection<URL> findExternalResourcesForConcept(Value concept)
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
	
	private String createIRIQuery(Value concept) {
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
        String authResourceIdentifier = authoritativeConcepts.getAuthResourceIdentifier();

        if (authResourceIdentifier != null && !authResourceIdentifier.isEmpty()) {
			return !url.toString().toLowerCase().contains(authResourceIdentifier.toLowerCase());
		}
		
		throw new IllegalArgumentException("external resource identifier must not be null or empty");
	}
	
	private boolean isNonSkosURL(URL url) {
		return !url.toString().contains(SparqlPrefix.SKOS.getNameSpace());
	}
	
	private Collection<Value> extractUnlinkedConcepts() {
		Collection<Value> unlinkedConcepts = new HashSet<Value>();
		
		for (Value concept : extResourcesForConcept.keySet()) {
			if (extResourcesForConcept.get(concept).isEmpty()) {
				unlinkedConcepts.add(concept);
			}
		}
		
		return unlinkedConcepts;
	}

}
