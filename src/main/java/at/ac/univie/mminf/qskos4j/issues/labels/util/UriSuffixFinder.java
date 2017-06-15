package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.eclipse.rdf4j.model.Value;

import java.util.Collection;

public class UriSuffixFinder {

    public static boolean isPartOfConflict(Collection<LabelConflict> conflicts, String uriSuffix)
    {
        if (conflicts != null) {
            for (LabelConflict conflict : conflicts) {
                for (Value resource : conflict.getAffectedResources()) {
                    if (resource.stringValue().endsWith(uriSuffix)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
