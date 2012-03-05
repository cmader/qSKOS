package at.ac.univie.mminf.qskos4j.criteria;

import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class LexicalRelationsFinder extends Criterion {

	private long relationsCount = 0;
	
	public LexicalRelationsFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}
	
	public long findLexicalRelationsCount(Set<URI> allConcepts) 
		throws OpenRDFException
	{		
		for (URI concept : allConcepts) {
			TupleQueryResult result = queryRepository(createLexicalLabelQuery(concept));
			addToRelationsCount(result);
		}
		
		return relationsCount;
	}
	
	private String createLexicalLabelQuery(URI concept) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDF +" "+ SparqlPrefix.RDFS +
			"SELECT ?labelType ?value WHERE {" +
				"<" +concept.stringValue()+ "> ?labelType ?value ." +
				"{?labelType rdfs:subPropertyOf* skos:prefLabel}" +
				"UNION" +
				"{?labelType rdfs:subPropertyOf* skos:altLabel}" +
				"UNION" +
				"{?labelType rdfs:subPropertyOf* skos:hiddenLabel}" +
			"}";	
	}
	
	private void addToRelationsCount(TupleQueryResult result) throws QueryEvaluationException 
	{
		while (result.hasNext()) {
			relationsCount++;
			result.next();
		}	
	}

}
