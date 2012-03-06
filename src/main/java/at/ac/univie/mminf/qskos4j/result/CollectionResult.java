package at.ac.univie.mminf.qskos4j.result;

import java.util.Collection;

public class CollectionResult<T> extends Result<Collection<T>> {

	public CollectionResult(Collection<T> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return "count: " +getData().size();
	}

	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
