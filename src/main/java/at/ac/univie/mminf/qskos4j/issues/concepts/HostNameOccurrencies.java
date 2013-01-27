package at.ac.univie.mminf.qskos4j.issues.concepts;

import java.util.*;

class HostNameOccurrencies extends HashMap<String, Integer> {

    void put(String hostname) {
        Integer occurencies = get(hostname);
        put(hostname, occurencies == null ? 1 : ++occurencies);
    }

    String getMostOftenOccuringHostName() {
        SortedSet<Map.Entry<String, Integer>> sortedEntries = new TreeSet<Map.Entry<String, Integer>>(
                new Comparator<Map.Entry<String, Integer>>()
                {
                    @Override
                    public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        sortedEntries.addAll(entrySet());
        if (!sortedEntries.isEmpty()) {
            return sortedEntries.first().getKey();
        }

        return "";
    }
}
