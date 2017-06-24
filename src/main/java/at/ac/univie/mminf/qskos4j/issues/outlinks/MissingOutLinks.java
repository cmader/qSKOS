package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.IssueDescriptor;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleIRI;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;

import java.util.*;

/**
 * Finds concepts without links to "external" resources (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_OutLinks">Missing Out-Links</a>.
 */
public class MissingOutLinks extends Issue<CollectionResult<Resource>> {

	private Map<Resource, Collection<IRI>> extResourcesForConcept;
    private AuthoritativeConcepts authoritativeConcepts;
	
	public MissingOutLinks(AuthoritativeConcepts authoritativeConcepts) {
		super(new IssueDescriptor.Builder(
            "mol",
            "Missing Out-Links",
            "Finds concepts that are not linked to other vocabularies on the Web",
            IssueDescriptor.IssueType.ANALYTICAL)
				.weblink("https://github.com/cmader/qSKOS/wiki/Quality-Issues#missing-out-links")
				.dependentIssue(authoritativeConcepts)
				.build()
        );

        this.authoritativeConcepts = authoritativeConcepts;
	}

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
		extResourcesForConcept = new HashMap<>();

		findResourcesForConcepts(authoritativeConcepts.getResult().getData());
		
		return new CollectionResult<>(extractUnlinkedConcepts());
	}

    private void findResourcesForConcepts(Collection<Resource> concepts) throws RDF4JException {
		Iterator<Resource> conceptIt = new MonitoredIterator<>(concepts, progressMonitor, "finding resources");

		while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
			extResourcesForConcept.put(concept, extractExternalResources(getIRIsOfConcept(concept)));
        }
	}

    private Collection<IRI> getIRIsOfConcept(Resource concept) throws RepositoryException {
        Collection<IRI> urisForConcept = new ArrayList<>();

        RepositoryResult<Statement> conceptAsSubject = repCon.getStatements(concept, null, null, false);
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

    private void addToUriCollection(Value value, Collection<IRI> uris) {
        if (value instanceof IRI) uris.add((IRI) value);
    }
	
	private Collection<IRI> extractExternalResources(Collection<IRI> allResources) throws RDF4JException {
		Collection<IRI> validExternalResources = new HashSet<>();

		for (IRI uri : allResources) {
			if (isExternalResource(uri) && isNonSkosURL(uri)) {
				validExternalResources.add(uri);
			}
		}
		
		return validExternalResources;
	}
	
	private boolean isExternalResource(IRI url) throws RDF4JException {
        String authResourceIdentifier = authoritativeConcepts.getAuthResourceIdentifier();

        if (authResourceIdentifier != null && !authResourceIdentifier.isEmpty()) {
			return !url.toString().toLowerCase().contains(authResourceIdentifier.toLowerCase());
		}
		
		throw new IllegalArgumentException("external resource identifier must not be null or empty");
	}
	
	private boolean isNonSkosURL(IRI url) {
		return !url.toString().contains(SparqlPrefix.SKOS.getNameSpace());
	}
	
	private Collection<Resource> extractUnlinkedConcepts() {
		Collection<Resource> unlinkedConcepts = new HashSet<>();
		
		for (Resource concept : extResourcesForConcept.keySet()) {
			if (extResourcesForConcept.get(concept).isEmpty()) {
				unlinkedConcepts.add(concept);
			}
		}
		
		return unlinkedConcepts;
	}

}
