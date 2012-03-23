package at.ac.univie.mminf.qskos4j.criteria;

import java.util.Collection;
import java.util.HashSet;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import at.ac.univie.mminf.qskos4j.result.general.CollectionResult;
import at.ac.univie.mminf.qskos4j.util.Pair;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;

public class RedundantAssociativeRelationsFinder extends Criterion {

	public RedundantAssociativeRelationsFinder(VocabRepository vocabRepository) {
		super(vocabRepository);
	}

	public CollectionResult<Pair<URI>> findValuelessAssociativeRelations() throws OpenRDFException 
	{
		Collection<Pair<URI>> redundantAssociativeRelations = new HashSet<Pair<URI>>();
		
		TupleQueryResult result = vocabRepository.query(createRedundantAssociativeRelationsQuery());
		generateResultsList(redundantAssociativeRelations, result);
		
		return new CollectionResult<Pair<URI>>(redundantAssociativeRelations);
	}
	
	public CollectionResult<Pair<URI>> findNotAssociatedSiblings() throws OpenRDFException 
	{
		Collection<Pair<URI>> notAssociatedSiblings = new HashSet<Pair<URI>>();
		
		TupleQueryResult result = vocabRepository.query(createNotAssociatedSiblingsQuery());
		generateResultsList(notAssociatedSiblings, result);
		
		return new CollectionResult<Pair<URI>>(notAssociatedSiblings);		
	}
	
	private String createRedundantAssociativeRelationsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?parent ?child ?otherchild "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"WHERE {?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?child . " +
				"?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?otherchild . " +
				"?child skos:related|skos:relatedMatch ?otherchild . }";
	}
	
	private String createNotAssociatedSiblingsQuery() {
		return SparqlPrefix.SKOS +
			"SELECT ?parent ?child ?otherchild "+
			"FROM <" +vocabRepository.getVocabContext()+ "> "+
			"WHERE {?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?child . " +
			"?parent skos:narrower|skos:narrowMatch|^skos:broader|^skos:broadMatch ?otherchild . " +
			"FILTER (!sameTerm(?child, ?otherchild) && " +
				"NOT EXISTS {?child ?p ?otherchild} &&" +
				"NOT EXISTS {?otherchild ?p ?child})}";
	}
	
	private void generateResultsList(Collection<Pair<URI>> allResults, TupleQueryResult result) 
		throws QueryEvaluationException
	{
		while (result.hasNext()) {
			BindingSet queryResult = result.next();
			URI child = (URI) queryResult.getValue("child");
			URI otherchild = (URI) queryResult.getValue("otherchild");

			allResults.add(new Pair<URI>(child, otherchild));
		}
	}
		
}
