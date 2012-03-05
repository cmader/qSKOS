package at.ac.univie.mminf.qskos4j.criteria;

import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class RelationStatisticsFinder extends Criterion {

	public RelationStatisticsFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public long findLexicalRelationsCount(Set<URI> allConcepts) 
		throws OpenRDFException
	{	
		long relationsCount = 0; 
		
		for (URI concept : allConcepts) {
			TupleQueryResult result = queryRepository(createLexicalLabelQuery(concept));
			relationsCount += countResults(result);
		}
		
		return relationsCount;
	}
	
	private String createLexicalLabelQuery(URI concept) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT ?labelType ?value WHERE {" +
				"<" +concept.stringValue()+ "> ?labelType ?value ." +
				"{?labelType rdfs:subPropertyOf* skos:prefLabel}" +
				"UNION" +
				"{?labelType rdfs:subPropertyOf* skos:altLabel}" +
				"UNION" +
				"{?labelType rdfs:subPropertyOf* skos:hiddenLabel}" +
			"}";	
	}
			
	private long countResults(TupleQueryResult result) throws QueryEvaluationException 
	{
		long count = 0;
		
		while (result.hasNext()) {
			count++;
			result.next();
		}
		
		return count;
	}
	
	public long findSemanticRelations() 
		throws OpenRDFException
	{
		TupleQueryResult result = queryRepository(createSemanticRelationsQuery());
		return countResults(result);
	}

	private String createSemanticRelationsQuery() {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT * WHERE {" +
				"?concept ?relationType ?otherConcept ."+
				"?relationType rdfs:subPropertyOf* skos:semanticRelation ."+
			"}";	
	}
	
}
