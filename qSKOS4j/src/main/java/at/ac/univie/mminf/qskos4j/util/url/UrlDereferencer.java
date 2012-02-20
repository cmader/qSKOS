package at.ac.univie.mminf.qskos4j.util.url;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlDereferencer {

	private final Logger logger = LoggerFactory.getLogger(UrlDereferencer.class);
	private final int CONNECTION_TIMOUT_MILLIS = 60000;
	
	public String getContentType(URL url) 
		throws UrlNotDereferencableException, NoContentTypeProvidedException
	{
		try {
			HttpResponse response = sendRequest(url);
			
			if (isValidResponse(url, response)) {
				Header contentType = response.getFirstHeader("Content-Type");
				if (contentType == null || contentType.getValue().isEmpty()) {
					throw new NoContentTypeProvidedException(); 
				}
				
				return contentType.getValue();
			}
		}
		catch (Exception e) {
			// fall through
		}
		throw new UrlNotDereferencableException(url);
	}
	
	private HttpResponse sendRequest(URL url) 
		throws URISyntaxException, ClientProtocolException, IOException 
	{
		logger.debug("dereferencing: " +url.toString());
		
		HttpGet httpGet = new HttpGet(url.toURI());
		httpGet.setHeader("Accept", "application/rdf+xml,text/html,application/xhtml+xml,application/xml,text/plain,*/*");
		return createParmeterizedHttpClient().execute(httpGet);
	}
	
	private AbstractHttpClient createParmeterizedHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMOUT_MILLIS);
		
		return httpClient;
	}
	
	private boolean isValidResponse(URL dereferencedUrl, HttpResponse response) 
	{
		int statusCode = response.getStatusLine().getStatusCode();
		logger.debug("response status: " +statusCode);
		
		if (Integer.toString(statusCode).startsWith("4") || 
			Integer.toString(statusCode).startsWith("5")) 
		{
			return false;
		}
		else {
			switch (statusCode) {
			case 200:
				return true;
			default:
				throw new UnhandledHTTPResponseException(statusCode);
			}
		}
	}
	
}
