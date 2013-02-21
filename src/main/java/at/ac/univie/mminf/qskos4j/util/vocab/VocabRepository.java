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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Deprecated
public class VocabRepository {

    private final Logger logger = LoggerFactory.getLogger(VocabRepository.class);
	private Repository repository;

	public VocabRepository(File rdfFile,String baseURI,RDFFormat dataFormat) throws RepositoryException
    {
		createRepositoryForFile();
        RepositoryConnection repCon = repository.getConnection();

        try {
            repCon.add(rdfFile, baseURI, dataFormat);
        }
        catch (Exception e) {
            logger.error("Could not add RDF data from file to temporary repository");
        }
        finally {
            repCon.close();
        }
    }

    public VocabRepository(Repository repository) throws OpenRDFException, IOException
    {
        this.repository = repository;
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
	}
	
	private String createDataDirName() {
		return System.getProperty("java.io.tmpdir") + 
			File.separator + 
			System.currentTimeMillis();
	}

    @Deprecated
    public TupleQueryResult query(String sparqlQuery) throws OpenRDFException {
        TupleQuery tupleQuery = repository.getConnection().prepareTupleQuery(QueryLanguage.SPARQL, sparqlQuery);
        return tupleQuery.evaluate();
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
	
	private void addSkosXlLabels(String skosXlProperty, String skosProperty) throws OpenRDFException
    {
        RepositoryConnection repCon = repository.getConnection();

        try {
    		GraphQuery graphQuery = createSkosXlGraphQuery(repCon, skosXlProperty, skosProperty);
            GraphQueryResult result = graphQuery.evaluate();

            while (result.hasNext()) {
                Statement statement = result.next();
                repCon.add(statement);
            }
        }
        finally {
            repCon.close();
        }
	}
	
	private GraphQuery createSkosXlGraphQuery(
        RepositoryConnection connection,
        String skosXlProperty,
        String skosProperty) throws OpenRDFException
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
