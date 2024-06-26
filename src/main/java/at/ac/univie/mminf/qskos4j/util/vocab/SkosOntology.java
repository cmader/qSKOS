package at.ac.univie.mminf.qskos4j.util.vocab;

import org.eclipse.rdf4j.RDF4JException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class SkosOntology {

    private final static Logger logger = LoggerFactory.getLogger(SkosOntology.class);

    public final static String SKOS_BASE_IRI = "https://www.w3.org/2004/02/skos/core";
    public final static String SKOS_ONTO_IRI = "https://www.w3.org/2009/08/skos-reference/skos.rdf";

    private final static SkosOntology ourInstance = new SkosOntology();
    private static Repository skosRepo;

    public static SkosOntology getInstance() {
        if (skosRepo == null) {
            try {
                ourInstance.createSkosRepo();
            }
            catch (Exception e) {
                logger.error("Error creating SKOS repository", e);
            }
        }
        return ourInstance;
    }

    private void createSkosRepo() throws RDF4JException, IOException {
        skosRepo = new SailRepository(new MemoryStore());
        skosRepo.initialize();

        try (RepositoryConnection repCon = skosRepo.getConnection()) {
            repCon.add(new URL(SKOS_ONTO_IRI), SKOS_BASE_IRI, RDFFormat.RDFXML);
        }
    }

    private SkosOntology() {
    }

    public IRI getUri(String element) {
        ValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(SKOS_BASE_IRI +"#"+ element);
    }

    public Repository getRepository() {
        return skosRepo;
    }

    public boolean isSkosResource(Resource resource) {
        return resource.stringValue().startsWith(SparqlPrefix.SKOS.getNameSpace());
    }

}
