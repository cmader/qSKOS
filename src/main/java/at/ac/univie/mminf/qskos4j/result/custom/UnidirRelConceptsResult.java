package at.ac.univie.mminf.qskos4j.result.custom;

import java.util.Map;

import org.openrdf.model.URI;

import at.ac.univie.mminf.qskos4j.result.Result;
import at.ac.univie.mminf.qskos4j.result.general.ConceptPairsResult;
import at.ac.univie.mminf.qskos4j.util.Pair;

public class UnidirRelConceptsResult extends Result<Map<Pair<URI>, String>> {

	public UnidirRelConceptsResult(Map<Pair<URI>, String> data) {
		super(data);
	}

	@Override
	public String getShortReport() {
		return new ConceptPairsResult(getData().keySet()).getShortReport();
	}

	@Override
	public String getExtensiveReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
