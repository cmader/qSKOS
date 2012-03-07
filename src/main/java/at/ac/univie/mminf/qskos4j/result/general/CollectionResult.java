package at.ac.univie.mminf.qskos4j.result.general;

import java.util.Collection;

import at.ac.univie.mminf.qskos4j.result.Result;

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
		return getData().toString();
	}

}
