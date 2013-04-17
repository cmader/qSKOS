package at.ac.univie.mminf.qskos4j.issues.outlinks;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.AuthoritativeConcepts;
import at.ac.univie.mminf.qskos4j.report.CollectionReport;
import at.ac.univie.mminf.qskos4j.report.Report;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import java.util.*;

/**
 * Finds concepts without links to "external" resources (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Missing_OutLinks">Missing Out-Links</a>.
 */
public class MissingOutLinks extends Issue<Collection<Value>> {

	private Map<Value, Collection<URI>> extResourcesForConcept;
    private AuthoritativeConcepts authoritativeConcepts;
	
	public MissingOutLinks(AuthoritativeConcepts authoritativeConcepts) {
		super("mol",
              "Missing Out-Links",
              "Finds concepts that are not linked to other vocabularies on the Web",
              IssueType.ANALYTICAL
        );

        this.authoritativeConcepts = authoritativeConcepts;
	}

    @Override
    public Collection<Value> prepareData() throws OpenRDFException {
		extResourcesForConcept = new HashMap<Value, Collection<URI>>();

		findResourcesForConcepts(authoritativeConcepts.getPreparedData());
		
		return extractUnlinkedConcepts();
	}

    @Override
    protected Report prepareReport(Collection<Value> preparedData) {
        return new CollectionReport<Value>(preparedData);
    }

    private void findResourcesForConcepts(Collection<Value> concepts) throws RepositoryException {
		Iterator<Value> conceptIt = new MonitoredIterator<Value>(concepts, progressMonitor, "finding resources");

		while (conceptIt.hasNext()) {
            Value concept = conceptIt.next();
			extResourcesForConcept.put(concept, extractExternalResources(getURIsOfConcept(concept)));
        }
	}

    private Collection<URI> getURIsOfConcept(Value concept) throws RepositoryException {
        Collection<URI> urisForConcept = new ArrayList<URI>();

        if (concept instanceof Resource) {
            RepositoryResult<Statement> conceptAsSubject = repCon.getStatements((Resource) concept, null, null, false);
            while (conceptAsSubject.hasNext()) {
                Value object = conceptAsSubject.next().getObject();
                addToUriCollection(object, urisForConcept);
            }
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
	
	private Collection<URI> extractExternalResources(Collection<URI> allResources) {
		Collection<URI> validExternalResources = new HashSet<URI>();
		
		for (URI uri : allResources) {
			if (isExternalResource(uri) && isNonSkosURL(uri)) {
				validExternalResources.add(uri);
			}
		}
		
		return validExternalResources;
	}
	
	private boolean isExternalResource(URI url) {
        String authResourceIdentifier = authoritativeConcepts.getAuthResourceIdentifier();

        if (authResourceIdentifier != null && !authResourceIdentifier.isEmpty()) {
			return !url.toString().toLowerCase().contains(authResourceIdentifier.toLowerCase());
		}
		
		throw new IllegalArgumentException("external resource identifier must not be null or empty");
	}
	
	private boolean isNonSkosURL(URI url) {
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
