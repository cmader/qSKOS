package at.ac.univie.mminf.qskos4j.result;

import java.util.Collection;

import org.openrdf.model.URI;

public class UriCollectionResult extends Result<Collection<URI>> {

	public UriCollectionResult(Collection<URI> data) {
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
