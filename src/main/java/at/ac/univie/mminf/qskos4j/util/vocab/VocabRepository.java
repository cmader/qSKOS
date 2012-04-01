package at.ac.univie.mminf.qskos4j.util.vocab;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

public class VocabRepository {

	public final String SKOS_GRAPH_URL = "http://www.w3.org/2009/08/skos-reference/skos.rdf";
	private final String VOCAB_DEFAULT_URL = "http://localhost/uploads/";
	
	private final String SKOS_BASE_URI = "http://www.w3.org/2004/02/skos/core";
	private Repository repository;
	private RepositoryConnection connection;
	private File rdfFile;
	private String queryEndpointUrl;
	
	public VocabRepository(
		File rdfFile,
		String baseURI,
		RDFFormat dataFormat) throws OpenRDFException, IOException
	{
		this.rdfFile = rdfFile;
		
		createRepositoryForFile();
		addSkosAndUserData(rdfFile, baseURI, dataFormat);
	}
				
	public Repository getRepository() {
		return repository;
	}
	
	public URI getVocabContext() {
		if (rdfFile != null) {
			return new URIImpl(VOCAB_DEFAULT_URL + rdfFile.getName());
		}
		if (queryEndpointUrl != null) {
			return new URIImpl(queryEndpointUrl);
		}
		
		throw new IllegalArgumentException("no vocabulary file or sparql endpoint given");
	}
	
	private void createRepositoryForFile() throws RepositoryException {
		File tempDir = new File(createDataDirName());
		repository = new SailRepository(new MemoryStore(tempDir));
		repository.initialize();
	}
	
	private String createDataDirName() {
		return System.getProperty("java.io.tmpdir") + 
			File.separator + 
			System.currentTimeMillis();
	}
	
	private void addSkosAndUserData(File rdfFile,
		String baseURI,
		RDFFormat dataFormat) 
		throws RepositoryException, RDFParseException, IOException 
	{
		connection = repository.getConnection();
		connection.add(rdfFile, baseURI, dataFormat, getVocabContext());
		connection.add(
			new URL(SKOS_GRAPH_URL), 
			SKOS_BASE_URI,
			RDFFormat.RDFXML,
			new URIImpl(SKOS_GRAPH_URL));
	}
	
	public TupleQueryResult query(String sparqlQuery) 
		throws RepositoryException, MalformedQueryException, QueryEvaluationException 
	{
		TupleQuery graphQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
		return graphQuery.evaluate();
	}
		
	public void enableSkosXlSupport() 
		throws OpenRDFException
	{
		addSkosXlLabels("skosxl:prefLabel", "skos:prefLabel");
		addSkosXlLabels("skosxl:altLabel", "skos:altLabel");
		addSkosXlLabels("skosxl:hiddenLabel", "skos:hiddenLabel");
	}
	
	private void addSkosXlLabels(String skosXlProperty, String skosProperty) 
		throws OpenRDFException
	{
		GraphQuery graphQuery = createSkosXlGraphQuery(skosXlProperty, skosProperty);
		
		GraphQueryResult result = graphQuery.evaluate();
		while (result.hasNext()) {
			Statement statement = result.next();
			connection.add(statement, getVocabContext());
		}		
	}
	
	private GraphQuery createSkosXlGraphQuery(String skosXlProperty, String skosProperty) 
		throws OpenRDFException
	{
		return connection.prepareGraphQuery(
			QueryLanguage.SPARQL,
			
			SparqlPrefix.SKOS +" "+ SparqlPrefix.SKOSXL+
			"CONSTRUCT { ?concept "+skosProperty+" ?label }"+ 
				"WHERE {"+ 
					"?concept " +skosXlProperty+ " ?xLabel ."+
					"?xLabel skosxl:literalForm ?label"+
				"}"
		);
	}
}
