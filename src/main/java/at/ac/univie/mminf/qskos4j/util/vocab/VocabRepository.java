package at.ac.univie.mminf.qskos4j.util.vocab;

import org.junit.Assert;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.query.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class VocabRepository {

	private Repository repository;
	private RepositoryConnection connection;

	public VocabRepository(File rdfFile,String baseURI,RDFFormat dataFormat) throws OpenRDFException, IOException
	{
		createRepositoryForFile();
        connection.add(rdfFile, baseURI, dataFormat);
	}

    public VocabRepository(Repository repository) throws OpenRDFException, IOException
    {
        this.repository = repository;
        connection = repository.getConnection();
    }

    public static VocabRepository setUpFromTestResource(String testFileName)
        throws OpenRDFException, IOException
    {
        URL conceptsUrl = VocabRepository.class.getResource("/" +testFileName);
        File conceptsFile = new File(conceptsUrl.getFile());
        Assert.assertNotNull(conceptsFile);
        return new VocabRepository(conceptsFile, null, RDFFormat.RDFXML);
    }
				
	public Repository getRepository() {
		return repository;
	}

	private void createRepositoryForFile() throws RepositoryException {
		File tempDir = new File(createDataDirName());
		repository = new SailRepository(new MemoryStore(tempDir));
		repository.initialize();
        connection = repository.getConnection();
	}
	
	private String createDataDirName() {
		return System.getProperty("java.io.tmpdir") + 
			File.separator + 
			System.currentTimeMillis();
	}

    public TupleQueryResult query(String sparqlQuery) throws OpenRDFException
    {
        TupleQuery graphQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
        return graphQuery.evaluate();
    }

    /**
     * If this is called, the local repository is complemented with SKOS lexical labels inferred from SKOSXL definitions
     * as described in the SKOS <a href="http://www.w3.org/TR/skos-reference/#S55">reference document</a> by the axioms
     * S55-S57
     *
     * @throws OpenRDFException if errors when initializing local repository
     */
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
			connection.add(statement);
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
