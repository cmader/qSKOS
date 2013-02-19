package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SkosOntology;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.Arrays;
import java.util.Iterator;

public class HierarchyGraphBuilder {

	private DirectedMultigraph<Value, NamedEdge> graph = new DirectedMultigraph<Value, NamedEdge>(NamedEdge.class);
	private VocabRepository vocabRepository;
	
	public HierarchyGraphBuilder(VocabRepository vocabRepository)
		throws OpenRDFException
	{
		this.vocabRepository = vocabRepository;
	}

	public DirectedMultigraph<Value, NamedEdge> createGraph() throws OpenRDFException
	{
        if (graph != null) {
            addResultsToGraph(findTriples(SkosOntology.SKOS_BROADER_PROPERTIES), false);
            addResultsToGraph(findTriples(SkosOntology.SKOS_NARROWER_PROPERTIES), true);
        }

		return graph;
	}
	
	private TupleQueryResult findTriples(URI[] skosHierarchyProperties) throws OpenRDFException
	{
		String query = createHierarchicalGraphQuery(skosHierarchyProperties);
		return vocabRepository.query(query);
	}
	
	private String createHierarchicalGraphQuery(URI[] skosHierarchyProperties) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource ?otherResource "+
			"WHERE {" +
                "{?resource ?hierProp ?otherResource}" +
                "UNION" +
                "{"+
                    "?resource ?hierarchyRelation ?otherResource ."+
                    "?hierarchyRelation rdfs:subPropertyOf ?hierProp ."+
                "}"+
                "FILTER (?hierProp IN " +createHierarchyPropertyList(skosHierarchyProperties) +")" +
            "}";
	}

    private String createHierarchyPropertyList(URI[] skosHierarchyProperties) {
        String propertyList = "(";
        Iterator<URI> propIt = Arrays.asList(skosHierarchyProperties).iterator();
        while (propIt.hasNext()) {
            propertyList += "<"+ propIt.next().stringValue() +">"+ (propIt.hasNext() ? ", " : ")");
        }
        return propertyList;
    }
	
	private void addResultsToGraph(TupleQueryResult result, boolean invertEdges) 
		throws QueryEvaluationException
	{	
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			
			addToGraph(
				bindingSet.getValue("resource"), 
				bindingSet.getValue("otherResource"), 
				invertEdges);
		}
	}
	
	private void addToGraph(
		Value resource, 
		Value otherResource,
		boolean invertEdges) 
	{
		Resource resourceNode = (Resource) resource;		
		graph.addVertex(resourceNode);
		
		Resource otherNode = (Resource) otherResource;
		graph.addVertex(otherNode);

		if (invertEdges) {
			graph.addEdge(otherNode, resourceNode, new NamedEdge());
		}
		else {
			graph.addEdge(resourceNode, otherNode, new NamedEdge());
		}
	}

    public VocabRepository getVocabRepository() {
        return vocabRepository;
    }

}
