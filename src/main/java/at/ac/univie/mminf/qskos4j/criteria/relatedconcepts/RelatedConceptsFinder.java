package at.ac.univie.mminf.qskos4j.criteria.relatedconcepts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.univie.mminf.qskos4j.criteria.Criterion;
import at.ac.univie.mminf.qskos4j.criteria.relatedconcepts.SkosLabel.LabelType;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class RelatedConceptsFinder extends Criterion {

	private final Logger logger = LoggerFactory.getLogger(RelatedConceptsFinder.class);
	private Set<RelatedConcepts> allRelatedConcepts;
	private Map<URI, Set<SkosLabel>> conceptLabels;
	
	public RelatedConceptsFinder(VocabRepository vocabRepository) 
	{
		super(vocabRepository);
	}
	
	public Set<RelatedConcepts> findRelatedConcepts(Set<URI> concepts) 
		throws OpenRDFException
	{
		if (allRelatedConcepts == null) {
			generateConceptsLabelMap(concepts);
			compareConceptLabels();
		}
		return allRelatedConcepts;
	}
	
	private void generateConceptsLabelMap(Set<URI> concepts) 
		throws OpenRDFException
	{
		conceptLabels = new HashMap<URI, Set<SkosLabel>>();
		
		logger.debug("Collecting label info");
		for (URI concept : concepts) {
			TupleQueryResult resultLabels1 = vocabRepository.query(createConceptLabelQuery(concept));
			Set<SkosLabel> skosLabels = createSkosLabelsFromResult(concept, resultLabels1);

			conceptLabels.put(concept, skosLabels);
		}
		logger.debug("Finished collecting label info");
	}
	
	private void compareConceptLabels() 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		allRelatedConcepts = new HashSet<RelatedConcepts>();
		List<URI> allConcepts = new ArrayList<URI>(conceptLabels.keySet());
		
		int i = 0;
		Iterator<URI> it = getMonitoredIterator(allConcepts);
		while (it.hasNext()) {
			URI concept1 = it.next();

			Set<SkosLabel> skosLabels1 = conceptLabels.get(concept1);
			
			for (int k = i + 1; k < allConcepts.size(); k++) {
				URI concept2 = allConcepts.get(k);
				Set<SkosLabel> skosLabels2 = conceptLabels.get(concept2);
												
				compareSkosLabels(skosLabels1, skosLabels2);
			}
			
			i++;
		}		
	}
	
	private String createConceptLabelQuery(URI concept) {
		return SparqlPrefix.SKOS+ 
			"SELECT ?prefLabel ?altLabel ?hiddenLabel "+
				"FROM <" +vocabRepository.getVocabContext()+ "> "+
				"WHERE {{<"+concept.stringValue()+"> skos:prefLabel ?prefLabel .} UNION "+
				"{<"+concept.stringValue()+"> skos:altLabel ?altLabel .} UNION "+
				"{<"+concept.stringValue()+"> skos:hiddenLabel ?hiddenLabel .}}";
	}
	
	private Set<SkosLabel> createSkosLabelsFromResult(URI concept, TupleQueryResult result) 
		throws QueryEvaluationException 
	{
		Set<SkosLabel> ret = new HashSet<SkosLabel>();
		
		while (result.hasNext()) {
			BindingSet queryResult = result.next();

			for (String bindingName : queryResult.getBindingNames()) {
				Literal literal = (Literal) queryResult.getValue(bindingName);
				
				SkosLabel skosLabel = new SkosLabel(
					concept,
					literal, 
					getLabelTypeForBindingName(bindingName));
					
				ret.add(skosLabel);
			}			
		}
		
		return ret;
	}	
	
	private LabelType getLabelTypeForBindingName(String bindingName) {
		if (bindingName.equals("prefLabel")) {
			return LabelType.PREF_LABEL;
		}
		if (bindingName.equals("altLabel")) {
			return LabelType.ALT_LABEL;
		}
		if (bindingName.equals("hiddenLabel")) {
			return LabelType.HIDDEN_LABEL;
		}
		return null;
	}
	
	private void compareSkosLabels(Set<SkosLabel> labels1, Set<SkosLabel> labels2) 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{		
		for (SkosLabel label1 : labels1) {
			for (SkosLabel label2 : labels2) {
				
				if (labelsAreSimilar(label1.getLiteral(), label2.getLiteral())) 
				{
					RelatedConcepts relatedConcepts =
						new RelatedConcepts(
							label1.getConcept(), 
							label2.getConcept(), 
							label1.getLiteral(), 
							label2.getLiteral(), 
							label1.getLabelType(), 
							label2.getLabelType(), 
							checkDirectlyConnected(label1.getConcept(), label2.getConcept()));
					
					allRelatedConcepts.add(relatedConcepts);
				}
			}
		}
	}

	private boolean labelsAreSimilar(Literal lit1, Literal lit2) {
		if (lit1.getLabel().isEmpty() || lit2.getLabel().isEmpty()) {
			return false;
		}
		return lit1.equals(lit2);
	}
	
	private boolean checkDirectlyConnected(URI concept1, URI concept2) 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		RepositoryConnection connection = vocabRepository.getRepository().getConnection();
		BooleanQuery graphQuery = connection.prepareBooleanQuery(
			QueryLanguage.SPARQL, 
			createConnectionQuery(concept1, concept2));
		return graphQuery.evaluate();
	}
	
	private String createConnectionQuery(URI concept1, URI concept2) {
		return "ASK {{<"+concept1.stringValue()+"> ?p <"+concept2.stringValue()+">} UNION" +
				"{<"+concept2.stringValue()+"> ?p <"+concept1.stringValue()+">}}";
	}
}
