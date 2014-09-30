package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchyGraphBuilder {

    private final Logger logger = LoggerFactory.getLogger(HierarchyGraphBuilder.class);

	private DirectedGraph<Resource, NamedEdge> graph;
    private RepositoryConnection repCon;

	public DirectedGraph<Resource, NamedEdge> createGraph() throws OpenRDFException
	{
        logger.info("Creating hierarchy graph");
        graph = new DefaultDirectedGraph<Resource, NamedEdge>(NamedEdge.class);

        addResultsToGraph(findTriples("skos:broaderTransitive"), false);
        addResultsToGraph(findTriples("skos:narrowerTransitive"), true);

        return graph;
	}
	
	private TupleQueryResult findTriples(String skosHierarchyProperty) throws OpenRDFException
	{
		    String query = createHierarchicalGraphQuery(skosHierarchyProperty);
            return repCon.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
	}
	
	private String createHierarchicalGraphQuery(String skosHierarchyProperty) {
		return SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
			"SELECT DISTINCT ?resource ?otherResource "+
			"WHERE {" +
                "?resource " +skosHierarchyProperty+ " ?otherResource ."+
            "}";
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

    public void setRepositoryConnection(RepositoryConnection repCon) {
        this.repCon = repCon;
    }

}
