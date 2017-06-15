package at.ac.univie.mminf.qskos4j.util.vocab;

import org.junit.Assert;
import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.URIImpl;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RepositoryBuilder {

    private final Logger logger = LoggerFactory.getLogger(RepositoryBuilder.class);

    private Repository repository;

    public Repository setUpFromTestResource(String testFileName) throws RDF4JException, IOException {
        URL conceptsUrl = RepositoryBuilder.class.getResource("/" +testFileName);
        File conceptsFile = new File(conceptsUrl.getFile());
        Assert.assertNotNull(conceptsFile);
        repository = setUpFromFile(conceptsFile, null, RDFFormat.RDFXML);
        return repository;
    }

    public Repository setUpFromFile(File rdfFile, String baseURI, RDFFormat dataFormat)
        throws RDF4JException, IOException
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

    private void addSkosOntology() throws RDF4JException, IOException {
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
     * @throws org.openrdf.RDF4JException if errors when initializing local repository
     */
    public void enableSkosXlSupport()
            throws RDF4JException
    {
        addSkosXlLabels("skosxl:prefLabel", "skos:prefLabel");
        addSkosXlLabels("skosxl:altLabel", "skos:altLabel");
        addSkosXlLabels("skosxl:hiddenLabel", "skos:hiddenLabel");
    }

    private void addSkosXlLabels(String skosXlProperty, String skosProperty) throws RDF4JException
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
            String skosProperty) throws RDF4JException
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
