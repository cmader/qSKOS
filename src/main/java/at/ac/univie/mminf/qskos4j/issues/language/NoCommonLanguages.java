package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.language.util.NoCommonLanguagesResult;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NoCommonLanguages extends Issue<NoCommonLanguagesResult> {

    private LanguageCoverage languageCoverage;
    private Set<String> commonLanguages;

    public NoCommonLanguages(LanguageCoverage languageCoverage) {
        super(languageCoverage,
              "ncl",
              "No Common Languages",
              "Checks for common languages in all concept literals",
              IssueType.ANALYTICAL);

        this.languageCoverage = languageCoverage;
    }

    @Override
    protected NoCommonLanguagesResult invoke() throws RDF4JException {
        Map<Resource, Collection<String>> langCovData = languageCoverage.getResult().getData();
        commonLanguages = new HashSet<>();

        if (!langCovData.isEmpty()) {
            for (Collection languages : langCovData.values()) {
                commonLanguages.addAll(languages);
            }

            findCommonLanguages();
        }

        return new NoCommonLanguagesResult(commonLanguages);
    }

    private void findCommonLanguages() throws RDF4JException {
        for (Map.Entry<Resource, Collection<String>> entry : languageCoverage.getResult().getData().entrySet()) {
            commonLanguages.retainAll(entry.getValue());
        }
    }
}
