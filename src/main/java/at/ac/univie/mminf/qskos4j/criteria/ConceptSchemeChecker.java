package at.ac.univie.mminf.qskos4j.criteria;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class ConceptSchemeChecker extends Criterion {

	public ConceptSchemeChecker(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public List<URI> findConceptSchemesWithoutTopConcept() 
		throws OpenRDFException
	{
		TupleQueryResult result = vocabRepository.query(createConceptSchemesWithoutTopConceptQuery());
		return createUriResultList(result, "conceptScheme");			
	}
	
	private String createConceptSchemesWithoutTopConceptQuery() {
		return SparqlPrefix.SKOS + 
			"SELECT DISTINCT ?conceptScheme WHERE " +
			"{" +
				"?concept skos:inScheme ?conceptScheme ." +
				"FILTER NOT EXISTS {?conceptScheme skos:hasTopConcept ?concept1}"+
				"FILTER NOT EXISTS {?concept2 skos:topConceptOf ?conceptScheme}" +
			"}";
	}
	
	private List<URI> createUriResultList(
		TupleQueryResult result, 
		String bindingName) throws OpenRDFException
	{
		List<URI> resultList = new ArrayList<URI>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI resource = (URI) queryResult.getValue(bindingName);
			resultList.add(resource);
		}
		
		return resultList;
	}
	
	public List<URI> findTopConceptsHavingBroaderConcept() 
		throws OpenRDFException
	{
		TupleQueryResult result = vocabRepository.query(createTopConceptsHavingBroaderConceptQuery());
		return createUriResultList(result, "topConcept");			
	}
	
	private String createTopConceptsHavingBroaderConceptQuery() {
		return SparqlPrefix.SKOS + 
			"SELECT DISTINCT ?topConcept WHERE " +
			"{" +
				"{?topConcept skos:topConceptOf ?conceptScheme1}" +
				"UNION" +
				"{?conceptScheme2 skos:hasTopConcept ?topConcept}" +
				"?topConcept skos:broader|skos:broaderTransitive|^skos:narrower|^skos:narrowerTransitive ?broaderConcept ." +
			"}";
	}
	
}
