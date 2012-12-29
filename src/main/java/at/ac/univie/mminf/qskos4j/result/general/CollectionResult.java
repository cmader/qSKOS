package at.ac.univie.mminf.qskos4j.result.general;

import at.ac.univie.mminf.qskos4j.result.Result;

import java.util.Collection;

/**
 * Result class that holds a collection of objects of interest
 * 
 * @author christian
 *
 * @param <T> type of the collection's content
 */
public class CollectionResult<T> extends Result<Collection<T>> {

	public CollectionResult(Collection<T> data) {
		super(data);
	}
		
	@Override
	public String getShortReport() {
		return "count: " +getData().size();
	}

	@Override
	public String getExtensiveReport() {
        StringBuilder report = new StringBuilder();

        for (T dataItem : getData()) {
            report.append("\n").append(dataItem.toString());
        }

        return report.toString();
	}

}
