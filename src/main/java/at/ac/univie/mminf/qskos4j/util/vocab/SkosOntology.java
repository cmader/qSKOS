package at.ac.univie.mminf.qskos4j.util.vocab;

import at.ac.univie.mminf.qskos4j.util.TupleQueryResultUtil;
import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class SkosOntology {

    private final Logger logger = LoggerFactory.getLogger(SkosOntology.class);

    private final String SKOS_GRAPH_URL = "http://www.w3.org/2009/08/skos-reference/skos.rdf";
    private final String SKOS_BASE_URI = "http://www.w3.org/2004/02/skos/core";

    private RepositoryConnection skosRepositoryConnection;

    private static SkosOntology ourInstance = new SkosOntology();

    public static SkosOntology getInstance() {
        return ourInstance;
    }

    private SkosOntology()
    {
        try {
            Repository skosRepository = new SailRepository(new MemoryStore());
            skosRepository.initialize();
            skosRepositoryConnection = skosRepository.getConnection();
            skosRepositoryConnection.add(
                    new URL(SKOS_GRAPH_URL),
                    SKOS_BASE_URI,
                    RDFFormat.RDFXML,
                    new URIImpl(SKOS_GRAPH_URL));
        }
        catch (Exception e) {
            logger.error("Could not create SKOS ontology repository", e);
        }
    }

    public String getSubPropertiesOfSemanticRelationsFilter(String bindingName) throws OpenRDFException
    {
        String semRelSubPropertiesQuery = SparqlPrefix.SKOS +" "+ SparqlPrefix.RDFS +
            "SELECT ?" +bindingName+ " WHERE {" +
                "?" +bindingName+ " rdfs:subPropertyOf+ skos:semanticRelation" +
            "}";

        TupleQuery tupleQuery = skosRepositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, semRelSubPropertiesQuery);
        return TupleQueryResultUtil.getFilterForBindingName(tupleQuery.evaluate(), bindingName);
    }

    public RepositoryConnection getConnection() {
        return skosRepositoryConnection;
    }
}
