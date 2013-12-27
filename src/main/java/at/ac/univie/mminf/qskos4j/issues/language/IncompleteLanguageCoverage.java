package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.concepts.InvolvedConcepts;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Finds all concepts with incomplete language coverage (<a href="https://github.com/cmader/qSKOS/wiki/Quality-Issues#wiki-Incomplete_Language_Coverage">Incomplete Language Coverage</a>
 */
public class IncompleteLanguageCoverage extends Issue<IncompleteLangCovResult> {

    private final Logger logger = LoggerFactory.getLogger(IncompleteLanguageCoverage.class);

	private Map<Resource, Collection<String>> languageCoverage, incompleteLanguageCoverage;
	private Set<String> distinctLanguages;
    private InvolvedConcepts involvedConcepts;

    public IncompleteLanguageCoverage(InvolvedConcepts involvedConcepts) {
        super(involvedConcepts,
            "ilc",
            "Incomplete Language Coverage",
            "Finds concepts lacking description in languages that are present for other concepts",
            IssueType.ANALYTICAL,
            new URIImpl("https://github.com/cmader/qSKOS/wiki/Quality-Issues#incomplete-language-coverage")
        );

        this.involvedConcepts = involvedConcepts;
    }

    @Override
    protected IncompleteLangCovResult invoke() throws OpenRDFException {
		incompleteLanguageCoverage = new HashMap<Resource, Collection<String>>();

        languageCoverage = new LanguageCoverage().findLanguageCoverage(
            involvedConcepts.getResult().getData(),
            progressMonitor,
            repCon);
        findDistinctLanguages();
		generateIncompleteLanguageCoverageMap();
		
		return new IncompleteLangCovResult(incompleteLanguageCoverage);
	}

    private void findDistinctLanguages() {
        distinctLanguages = new HashSet<String>();
        for (Collection languages : languageCoverage.values()) {
            distinctLanguages.addAll(languages);
        }
    }

	private void generateIncompleteLanguageCoverageMap() {
		incompleteLanguageCoverage = new HashMap<Resource, Collection<String>>();
		
		for (Resource resource : languageCoverage.keySet()) {
			Collection<String> coveredLanguages = languageCoverage.get(resource);
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
