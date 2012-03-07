package at.ac.univie.mminf.qskos4j.result;

import java.util.Map;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.util.Pair;

public class UrcResult extends Result<Map<Pair<URI>, String>> {

	public UrcResult(Map<Pair<URI>, String> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new ConceptPairsResult(getData().keySet()).getShortReport();
	}

	@Override
	public String getDetailedReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
