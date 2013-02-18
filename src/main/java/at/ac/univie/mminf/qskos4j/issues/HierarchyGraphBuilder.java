package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import at.ac.univie.mminf.qskos4j.util.vocab.VocabRepository;
import org.jgrapht.graph.DirectedMultigraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import java.util.Arrays;
import java.util.Iterator;

public class HierarchyGraphBuilder {

	private enum HierarchyStyle {BROADER, NARROWER}

	private final String[] skosBroaderProperties = {"skos:broader", "skos:broaderTransitive", "skos:broadMatch"};
	private final String[] skosNarrowerProperties = {"skos:narrower", "skos:narrowerTransitive", "skos:narrowMatch"};

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
            addResultsToGraph(findTriples(HierarchyStyle.BROADER), false);
            addResultsToGraph(findTriples(HierarchyStyle.NARROWER), true);
        }

		return graph;
	}
	
	private TupleQueryResult findTriples(HierarchyStyle hierarchyStyle) throws OpenRDFException
	{
		String[] skosHierarchyProperties = null;
		switch (hierarchyStyle) {
		case BROADER:
			skosHierarchyProperties = skosBroaderProperties;
			break;
		case NARROWER:
			skosHierarchyProperties = skosNarrowerProperties;
		}
		
		String query = createHierarchicalGraphQuery(skosHierarchyProperties);
		return vocabRepository.query(query);
	}
	
	private String createHierarchicalGraphQuery(String[] skosHierarchyProperties) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource ?otherResource "+
			"WHERE {" +
                "{?resource " +createHierarchyPropertyPath(skosHierarchyProperties)+ " ?otherResource}" +
                "UNION" +
                "{"+
                    "?resource ?hierarchyRelation ?otherResource ."+
                    "?hierarchyRelation rdfs:subPropertyOf ?skosHierarchyRelation ."+
                    "FILTER (?skosHierarchyRelation IN " +createHierarchyPropertyList(skosHierarchyProperties) +")" +
                "}"+
            "}";
	}

    private String createHierarchyPropertyPath(String[] skosHierarchyProperties) {
        String propertyPath = "(";
        Iterator<String> propIt = Arrays.asList(skosHierarchyProperties).iterator();
        while (propIt.hasNext()) {
            propertyPath += propIt.next() + (propIt.hasNext() ? "|" : ")");
        }
        return propertyPath;
    }

    private String createHierarchyPropertyList(String[] skosHierarchyProperties) {
        String propertyList = "(";
        Iterator<String> propIt = Arrays.asList(skosHierarchyProperties).iterator();
        while (propIt.hasNext()) {
            propertyList += propIt.next() + (propIt.hasNext() ? ", " : ")");
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
