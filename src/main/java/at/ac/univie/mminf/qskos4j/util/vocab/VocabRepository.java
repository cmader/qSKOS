package at.ac.univie.mminf.qskos4j.util.vocab;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

public class VocabRepository {

	public final String SKOS_GRAPH_URL = "http://www.w3.org/2009/08/skos-reference/skos.rdf";
	private final String VOCAB_DEFAULT_URL = "http://localhost/uploads/";
	
	private final String SKOS_BASE_URI = "http://www.w3.org/2004/02/skos/core";
	private Repository repository;
	private File rdfFile;
	private String queryEndpointUrl;
	
	public VocabRepository(
		File rdfFile,
		String baseURI,
		RDFFormat dataFormat) throws RepositoryException, RDFParseException, IOException
	{
		this.rdfFile = rdfFile;
		
		createRepositoryForFile();
		addSkosAndUserData(rdfFile, baseURI, dataFormat);
	}
	
	public VocabRepository(String queryEndpointUrl)
	{
		this.queryEndpointUrl = queryEndpointUrl;		
		repository = new SPARQLRepository(queryEndpointUrl);
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
		RepositoryConnection connection = repository.getConnection();
		
		try {
			connection.add(rdfFile, baseURI, dataFormat, getVocabContext());
			connection.add(
				new URL(SKOS_GRAPH_URL), 
				SKOS_BASE_URI,
				RDFFormat.RDFXML,
				new URIImpl(SKOS_GRAPH_URL));
		}
		finally {
			connection.close();
		}
	}
		
}
