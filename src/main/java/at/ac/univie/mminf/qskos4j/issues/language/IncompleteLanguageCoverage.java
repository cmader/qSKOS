package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Finds all concepts with incomplete language coverage (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Incomplete_Language_Coverage">Incomplete Language Coverage</a>
 */
public class IncompleteLanguageCoverage extends Issue<IncompleteLangCovResult> {

    private final Logger logger = LoggerFactory.getLogger(IncompleteLanguageCoverage.class);

	private Map<Resource, Collection<String>> incompleteLanguageCoverage;
    private LanguageCoverage languageCoverage;
	private Set<String> distinctLanguages;

    public IncompleteLanguageCoverage(LanguageCoverage languageCoverage) {
        super(languageCoverage,
            "ilc",
            "Incomplete Language Coverage",
            "Finds concepts lacking description in languages that are present for other concepts",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#incomplete-language-coverage")
        );

        this.languageCoverage = languageCoverage;
    }

    @Override
    protected IncompleteLangCovResult invoke() throws RDF4JException {
		incompleteLanguageCoverage = new HashMap<Resource, Collection<String>>();

        findDistinctLanguages();
		generateIncompleteLanguageCoverageMap();
		
		return new IncompleteLangCovResult(incompleteLanguageCoverage);
	}

    private void findDistinctLanguages() throws RDF4JException {
        distinctLanguages = new HashSet<String>();
        for (Collection languages : languageCoverage.getResult().getData().values()) {
            distinctLanguages.addAll(languages);
        }
    }

	private void generateIncompleteLanguageCoverageMap() throws RDF4JException {
		incompleteLanguageCoverage = new HashMap<Resource, Collection<String>>();

        Map<Resource, Collection<String>> langCovData = languageCoverage.getResult().getData();

		for (Resource resource : langCovData.keySet()) {
			Collection<String> coveredLanguages = langCovData.get(resource);
			Collection<String> notCoveredLanguages = getNotCoveredLanguages(coveredLanguages);
			if (!notCoveredLanguages.isEmpty()) {
				incompleteLanguageCoverage.put(resource, notCoveredLanguages);
			}
		}
	}
	
	private Collection<String> getNotCoveredLanguages(Collection<String> coveredLanguages) {
		Set<String> ret = new HashSet<String>(distinctLanguages);
		ret.removeAll(coveredLanguages);
		return ret;
	}

}
