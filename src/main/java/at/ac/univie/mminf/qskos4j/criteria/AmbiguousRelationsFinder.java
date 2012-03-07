package at.ac.univie.mminf.qskos4j.criteria;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.ConceptPairsResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class AmbiguousRelationsFinder extends Criterion {

	private Set<Pair<URI>> ambiguousRelations;
	
	public AmbiguousRelationsFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public ConceptPairsResult findAmbiguousRelations() 
		throws OpenRDFException
	{
		ambiguousRelations = new HashSet<Pair<URI>>();
		
		TupleQueryResult result = vocabRepository.query(createdAmbiguousRelationsQuery());
		addToResultsList(result);
		
		return new ConceptPairsResult(ambiguousRelations);
	}
	
	private String createdAmbiguousRelationsQuery() {
		return SparqlPrefix.SKOS + 
			"SELECT DISTINCT ?concept1 ?concept2 WHERE " +
			"{"+
				"{?concept1 skos:broader|skos:broaderTransitive|skos:broadMatch ?concept2}" +
				"UNION" +
				"{?concept1 skos:narrower|skos:narrowerTransitive|skos:narrowMatch ?concept2}" +
				
				"?concept1 skos:related|skos:relatedMatch|^skos:related|^skos:relatedMatch ?concept2" +
			"}";
	}
	
	private void addToResultsList(TupleQueryResult result) 
		throws OpenRDFException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI concept1 = (URI) queryResult.getValue("concept1");
			URI concept2 = (URI) queryResult.getValue("concept2");

			ambiguousRelations.add(new Pair<URI>(concept1, concept2));
		}
	}

}
