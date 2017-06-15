package at.ac.univie.mminf.qskos4j.issues;

import at.ac.univie.mminf.qskos4j.util.graph.NamedEdge;
import at.ac.univie.mminf.qskos4j.util.vocab.SparqlPrefix;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchyGraphBuilder {

    private final Logger logger = LoggerFactory.getLogger(HierarchyGraphBuilder.class);

	private DirectedGraph<Resource, NamedEdge> graph;
    private RepositoryConnection repCon;

	public DirectedGraph<Resource, NamedEdge> createGraph() throws RDF4JException
	{
        if (graph == null) {
            logger.info("Creating hierarchy graph");
            graph = new DefaultDirectedGraph<>(NamedEdge.class);

            addResultsToGraph(findTriples("skos:broaderTransitive"), false);
            addResultsToGraph(findTriples("skos:narrowerTransitive"), true);
        }

        return graph;
	}
	
	private TupleQueryResult findTriples(String skosHierarchyProperty) throws RDF4JException
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
        graph = null;
    }

}
