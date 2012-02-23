package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

@Ignore
public class QSkosTestCase {
	
	protected QSkos setUpInstance(String testFileName) 
		throws RepositoryException, RDFParseException, IOException 
	{
		URL conceptsUrl = getClass().getResource("/"+ testFileName);
		File conceptsFile = new File(conceptsUrl.getFile());
		Assert.assertNotNull(conceptsFile);
		
		return new QSkos(conceptsFile, RDFFormat.RDFXML);
	}
	
}
