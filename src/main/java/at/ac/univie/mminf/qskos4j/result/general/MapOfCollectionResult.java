package at.ac.univie.mminf.qskos4j.result.general;

import java.util.Collection;
import java.util.Map;

import at.ac.univie.mminf.qskos4j.result.Result;

public class MapOfCollectionResult<K, T> extends Result<Map<K, Collection<T>>> {

	public MapOfCollectionResult(Map<K, Collection<T>> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new CollectionResult<K>(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
