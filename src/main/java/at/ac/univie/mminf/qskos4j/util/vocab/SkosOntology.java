package at.ac.univie.mminf.qskos4j.util.vocab;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class SkosOntology {

    private final static Logger logger = LoggerFactory.getLogger(SkosOntology.class);

    public final static String SKOS_BASE_URI = "http://www.w3.org/2004/02/skos/core";
    public final static String SKOS_ONTO_URI = "http://www.w3.org/2009/08/skos-reference/skos.rdf";

    private static SkosOntology ourInstance = new SkosOntology();
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

    private void createSkosRepo() throws OpenRDFException, IOException {
        skosRepo = new SailRepository(new MemoryStore());
        skosRepo.initialize();

        RepositoryConnection repCon = skosRepo.getConnection();
        try {
            repCon.add(new URL(SKOS_ONTO_URI),
                SKOS_BASE_URI,
                RDFFormat.RDFXML);
        }
        finally {
            repCon.close();
        }
    }

    private SkosOntology() {
    }

    public URI getUri(String element) {
        return new URIImpl(SKOS_BASE_URI +"#"+ element);
    }

    public Repository getRepository() {
        return skosRepo;
    }

    public boolean isSkosResource(Resource resource) {
        return resource.stringValue().startsWith(SparqlPrefix.SKOS.getNameSpace());
    }

}
