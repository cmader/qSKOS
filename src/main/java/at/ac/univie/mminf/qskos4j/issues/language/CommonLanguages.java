package at.ac.univie.mminf.qskos4j.issues.language;

import at.ac.univie.mminf.qskos4j.issues.Issue;
import at.ac.univie.mminf.qskos4j.issues.language.util.LanguageCoverage;
import at.ac.univie.mminf.qskos4j.result.CollectionResult;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommonLanguages extends Issue<CollectionResult<String>> {

    private LanguageCoverage languageCoverage;
    private Set<String> commonLanguages;

    public CommonLanguages(LanguageCoverage languageCoverage) {
        super(languageCoverage,
              "clg",
              "Common Languages",
              "Checks for common languages in all concept literals",
              IssueType.ANALYTICAL);

        this.languageCoverage = languageCoverage;
    }

    @Override
    protected CollectionResult<String> invoke() throws OpenRDFException {
        Map<Resource, Collection<String>> langCovData = languageCoverage.getResult().getData();
        commonLanguages = new HashSet<String>();

        if (!langCovData.isEmpty()) {
            commonLanguages.addAll(langCovData.entrySet().iterator().next().getValue());
            findCommonLanguages();
        }

        return new CollectionResult<>(commonLanguages);
    }

    private void findCommonLanguages() throws OpenRDFException {
        for (Map.Entry<Resource, Collection<String>> entry : languageCoverage.getResult().getData().entrySet()) {
            commonLanguages.retainAll(entry.getValue());
        }
    }
}
