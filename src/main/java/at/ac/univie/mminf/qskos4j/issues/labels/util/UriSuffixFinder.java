package at.ac.univie.mminf.qskos4j.issues.labels.util;

import org.openrdf.model.Resource;

import java.util.Collection;

public class UriSuffixFinder {

    public static boolean isPartOfConflict(Collection<LabelConflict> conflicts, String uriSuffix)
    {
        if (conflicts != null) {
            for (LabelConflict conflict : conflicts) {
                for (Resource resource : conflict.getAffectedResources()) {
                    if (resource.stringValue().endsWith(uriSuffix)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
