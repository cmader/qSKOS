package at.ac.univie.mminf.qskos4j.util.url;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlDereferencer {

	private final Logger logger = LoggerFactory.getLogger(UrlDereferencer.class);
	private final int HTTP_GET_TIMOUT_MILLIS = 60000;
	
	public String getContentType(URL url) throws UrlNotDereferencableException {
		try {
			HttpResponse response = sendRequest(url);
			
			if (isValidResponse(response)) {
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
		throws URISyntaxException, IOException
	{
		logger.debug("dereferencing: " +url.toString());
		
		HttpGet httpGet = new HttpGet(url.toURI());
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml,text/plain,*/*;q=0.5");

        AbstractHttpClient httpClient = createParameterizedHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        httpClient.getConnectionManager().shutdown();

        return response;
	}
	
	private AbstractHttpClient createParameterizedHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, HTTP_GET_TIMOUT_MILLIS);
		params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, HTTP_GET_TIMOUT_MILLIS);
		
		return httpClient;
	}
	
	private boolean isValidResponse(HttpResponse response)
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
