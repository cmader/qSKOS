package at.ac.univie.mminf.qskos4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.openrdf.OpenRDFException;
import org.openrdf.rio.RDFFormat;

@Ignore
public class QSkosTestCase {
	
	protected QSkos setUpInstance(String testFileName) 
		throws OpenRDFException, IOException 
	{
		URL conceptsUrl = getClass().getResource("/"+ testFileName);
		File conceptsFile = new File(conceptsUrl.getFile());
		Assert.assertNotNull(conceptsFile);
		
		return new QSkos(conceptsFile, RDFFormat.RDFXML);
	}
	
}
