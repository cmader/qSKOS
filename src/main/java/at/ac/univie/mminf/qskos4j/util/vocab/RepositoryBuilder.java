package at.ac.univie.mminf.qskos4j.util.vocab;

import org.junit.Assert;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RepositoryBuilder {

    private final Logger logger = LoggerFactory.getLogger(RepositoryBuilder.class);

    private Repository repository;

    public Repository setUpFromTestResource(String testFileName) throws OpenRDFException, IOException {
        URL conceptsUrl = RepositoryBuilder.class.getResource("/" +testFileName);
        File conceptsFile = new File(conceptsUrl.getFile());
        Assert.assertNotNull(conceptsFile);
        repository = setUpFromFile(conceptsFile, null, RDFFormat.RDFXML);
        return repository;
    }

    public Repository setUpFromFile(File rdfFile, String baseURI, RDFFormat dataFormat)
        throws OpenRDFException, IOException
    {
        logger.info("Initializing evaluation repository for " +rdfFile.getName()+ "...");

        createRepositoryForFile();
        addSkosOntology();
        RepositoryConnection repCon = repository.getConnection();

        try {
            repCon.add(rdfFile, baseURI, dataFormat);
        }
        catch (Exception e) {
            throw new InvalidRdfException("Could not add RDF data from file to temporary repository");
        }
        finally {
            repCon.close();
        }

        return repository;
    }

    private void createRepositoryForFile() throws RepositoryException {
        File tempDir = new File(createDataDirName());
        repository = new SailRepository(new ForwardChainingRDFSInferencer(new MemoryStore(tempDir)));
        repository.initialize();
    }

    private void addSkosOntology() throws OpenRDFException, IOException {
        repository.getConnection().add(
            new URL(SkosOntology.SKOS_ONTO_URI),
            SkosOntology.SKOS_BASE_URI,
            RDFFormat.RDFXML,
            new URIImpl(SkosOntology.SKOS_ONTO_URI));
    }

    private String createDataDirName() {
        return System.getProperty("java.io.tmpdir") +
                File.separator +
                System.currentTimeMillis();
    }

    /**
     * If this is called, the local repository is complemented with SKOS lexical labels inferred from SKOSXL definitions
     * as described in the SKOS <a href="http://www.w3.org/TR/skos-reference/#S55">reference document</a> by the axioms
     * S55-S57
     *
     * @throws org.openrdf.OpenRDFException if errors when initializing local repository
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
