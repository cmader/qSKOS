package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.util.*;

/**
 * Finds concepts without links to "external" resources (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_OutLinks">Missing Out-Links</a>.
 */
public class MissingOutLinks extends Issue<CollectionResult<Resource>> {

	private Map<Resource, Collection<URI>> extResourcesForConcept;
    private AuthoritativeConcepts authoritativeConcepts;
	
	public MissingOutLinks(AuthoritativeConcepts authoritativeConcepts) {
		super(authoritativeConcepts,
            "mol",
            "Missing Out-Links",
            "Finds concepts that are not linked to other vocabularies on the Web",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#missing-out-links")
        );

        this.authoritativeConcepts = authoritativeConcepts;
	}

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
		extResourcesForConcept = new HashMap<Resource, Collection<URI>>();

		findResourcesForConcepts(authoritativeConcepts.getResult().getData());
		
		return new CollectionResult<Resource>(extractUnlinkedConcepts());
	}

    private void findResourcesForConcepts(Collection<Resource> concepts) throws RDF4JException {
		Iterator<Resource> conceptIt = new MonitoredIterator<Resource>(concepts, progressMonitor, "finding resources");

		while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
			extResourcesForConcept.put(concept, extractExternalResources(getURIsOfConcept(concept)));
        }
	}

    private Collection<URI> getURIsOfConcept(Resource concept) throws RepositoryException {
        Collection<URI> urisForConcept = new ArrayList<URI>();

        RepositoryResult<Statement> conceptAsSubject = repCon.getStatements((Resource) concept, null, null, false);
        while (conceptAsSubject.hasNext()) {
            Value object = conceptAsSubject.next().getObject();
            addToUriCollection(object, urisForConcept);
        }

        RepositoryResult<Statement> conceptAsObject = repCon.getStatements(null, null, concept, false);
        while (conceptAsObject.hasNext()) {
            Value object = conceptAsObject.next().getSubject();
            addToUriCollection(object, urisForConcept);
        }

        return urisForConcept;
    }

    private void addToUriCollection(Value value, Collection<URI> uris) {
        if (value instanceof URI) uris.add((URI) value);
    }
	
	private Collection<URI> extractExternalResources(Collection<URI> allResources) throws RDF4JException {
		Collection<URI> validExternalResources = new HashSet<URI>();
		
		for (URI uri : allResources) {
			if (isExternalResource(uri) && isNonSkosURL(uri)) {
				validExternalResources.add(uri);
			}
		}
		
		return validExternalResources;
	}
	
	private boolean isExternalResource(URI url) throws RDF4JException {
        String authResourceIdentifier = authoritativeConcepts.getAuthResourceIdentifier();

        if (authResourceIdentifier != null && !authResourceIdentifier.isEmpty()) {
			return !url.toString().toLowerCase().contains(authResourceIdentifier.toLowerCase());
		}
		
		throw new IllegalArgumentException("external resource identifier must not be null or empty");
	}
	
	private boolean isNonSkosURL(URI url) {
		return !url.toString().contains(SparqlPrefix.SKOS.getNameSpace());
	}
	
	private Collection<Resource> extractUnlinkedConcepts() {
		Collection<Resource> unlinkedConcepts = new HashSet<Resource>();
		
		for (Resource concept : extResourcesForConcept.keySet()) {
			if (extResourcesForConcept.get(concept).isEmpty()) {
				unlinkedConcepts.add(concept);
			}
		}
		
		return unlinkedConcepts;
	}

}
