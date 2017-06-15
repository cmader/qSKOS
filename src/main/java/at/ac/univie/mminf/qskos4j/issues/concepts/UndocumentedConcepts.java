package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Finds concepts lacking documentation information (
 * <a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Undocumented_Concepts">Undocumented Concepts</a>
 * ).
 */
public class UndocumentedConcepts extends Issue<CollectionResult<Resource>> {

    private final Logger logger = LoggerFactory.getLogger(UndocumentedConcepts.class);

    private AuthoritativeConcepts authoritativeConcepts;
	private String[] documentationProperties = {
		"skos:note", "skos:changeNote", "skos:definition", "skos:editorialNote",
		"skos:example", "skos:historyNote", "skos:scopeNote"
	};
	
    public UndocumentedConcepts(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts,
            "uc",
            "Undocumented Concepts",
            "Finds concepts that don't use any SKOS documentation properties",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#undocumented-concepts")
        );

        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected CollectionResult<Resource> invoke() throws RDF4JException {
		List<Resource> undocumentedConcepts = new ArrayList<Resource>();
		
		Iterator<Resource> conceptIt = new MonitoredIterator<Resource>(
            authoritativeConcepts.getResult().getData(),
            progressMonitor);

		while (conceptIt.hasNext()) {
            Resource concept = conceptIt.next();
			if (!isConceptDocumented(concept)) {
				undocumentedConcepts.add(concept);
			}
		}
		
		return new CollectionResult<Resource>(undocumentedConcepts);
	}

    private boolean isConceptDocumented(Value concept) {
		for (String docProperty : documentationProperties) {
			if (conceptHasProperty(concept, docProperty)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean conceptHasProperty(Value concept, String property)
	{
        try {
            BooleanQuery graphQuery = repCon.prepareBooleanQuery(
                QueryLanguage.SPARQL,
                createPropertyQuery(concept, property));
            return graphQuery.evaluate();
        }
        catch (RDF4JException e) {
            logger.error("Error finding documentation properties of concept '" +concept+ "'");
        }
        return false;
	}
	
	private String createPropertyQuery(Value concept, String property) {
		return SparqlPrefix.SKOS + "ASK {<"+concept.stringValue()+"> " +property+ "?o}";
	}

}
