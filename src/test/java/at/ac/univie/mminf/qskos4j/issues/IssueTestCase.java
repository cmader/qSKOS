package at.ac.univie.mminf.qskos4j.issues;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Ignore;
import org.openrdf.OpenRDFException;
import org.openrdf.rio.RDFFormat;

import at.ac.univie.mminf.qskos4j.QSkos;


@Ignore
public class IssueTestCase {
	
	protected QSkos setUpInstance(String testFileName) 
		throws OpenRDFException, IOException 
	{
		URL conceptsUrl = getClass().getResource("/"+ testFileName);
		File conceptsFile = new File(conceptsUrl.getFile());
		Assert.assertNotNull(conceptsFile);
		
		return new QSkos(conceptsFile, RDFFormat.RDFXML);
	}
	
}
