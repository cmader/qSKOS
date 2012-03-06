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
	
	public long findSemanticRelationsCount() 
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
	
	public long findAggregationRelationsCount() 
		throws OpenRDFException
	{
		TupleQueryResult result = queryRepository(createAggregationRelationsQuery());
		return countResults(result);
	}
	
	private String createAggregationRelationsQuery() {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT * WHERE {" +
				"?res1 ?relationType ?res2 ."+
				"{?relationType rdfs:subPropertyOf* skos:topConceptOf}" +
				"UNION" +
				"{?relationType rdfs:subPropertyOf* skos:hasTopConcept}" +
				"UNION" +
				"{?relationType rdfs:subPropertyOf* skos:inScheme}"+
				"UNION" +
				"{?relationType rdfs:subPropertyOf* skos:member}"+
				"UNION" +
				"{?relationType rdfs:subPropertyOf* skos:memberList}"+
			"}";
	}
	
	public long findConceptSchemeCount() 
		throws OpenRDFException
	{
		TupleQueryResult result = queryRepository(createConceptSchemeQuery());		
		return countResults(result);
	}
	
	private String createConceptSchemeQuery() {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +" "+ SparqlPrefix.RDF +
			"SELECT DISTINCT ?conceptScheme WHERE {" +
				"{?conceptScheme rdf:type/rdfs:subClassOf* skos:ConceptScheme}" +
				"UNION" +
				"{?conceptScheme ?hasTopConcept ?concept . ?hasTopConcept rdfs:subPropertyOf* skos:hasTopConcept}" +
				"UNION" +
				"{?concept ?topConceptOf ?conceptScheme . ?topConceptOf rdfs:subPropertyOf* skos:topConceptOf}" +
				"UNION" +
				"{?concept ?inScheme ?conceptScheme . ?inScheme rdfs:subPropertyOf* skos:inScheme}"+
			"}";	
	}
	
	public long findCollectionCount() {
		return 0;
	}
}
