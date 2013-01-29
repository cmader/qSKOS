package at.ac.univie.mminf.qskos4j.issues.concepts;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.progress.MonitoredIterator;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Value;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
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
public class UndocumentedConcepts extends Issue<CollectionResult<Value>> {

    private final Logger logger = LoggerFactory.getLogger(UndocumentedConcepts.class);

    private AuthoritativeConcepts authoritativeConcepts;
	private String[] documentationProperties = {
		"skos:note", "skos:changeNote", "skos:definition", "skos:editorialNote",
		"skos:example", "skos:historyNote", "skos:scopeNote"
	};
	
	private RepositoryConnection connection;

    public UndocumentedConcepts(AuthoritativeConcepts authoritativeConcepts) {
        super(authoritativeConcepts.getVocabRepository(),
              "uc",
              "Undocumented Concepts",
              "Finds concepts that don't use any SKOS documentation properties",
              IssueType.ANALYTICAL
        );
        this.authoritativeConcepts = authoritativeConcepts;
    }

    @Override
    protected CollectionResult<Value> invoke() throws OpenRDFException {
		connection = vocabRepository.getRepository().getConnection();
		List<Value> undocumentedConcepts = new ArrayList<Value>();
		
		Iterator<Value> conceptIt = new MonitoredIterator<Value>(
            authoritativeConcepts.getResult().getData(),
            progressMonitor);

		while (conceptIt.hasNext()) {
            Value concept = conceptIt.next();
			if (!isConceptDocumented(concept)) {
				undocumentedConcepts.add(concept);
			}
		}
		
		return new CollectionResult<Value>(undocumentedConcepts);
	}
	
	private boolean isConceptDocumented(Value concept)
		throws OpenRDFException 
	{		
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
            BooleanQuery graphQuery = connection.prepareBooleanQuery(
                QueryLanguage.SPARQL,
                createPropertyQuery(concept, property));
            return graphQuery.evaluate();
        }
        catch (OpenRDFException e) {
            logger.error("Error finding documentation properties of concept '" +concept+ "'");
        }
        return false;
	}
	
	private String createPropertyQuery(Value concept, String property) {
		return SparqlPrefix.SKOS + "ASK {<"+concept.stringValue()+"> " +property+ "?o}";
	}

}
