package at.ac.univie.mminf.qskos4j.util.url;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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

        HttpUriRequest request = RequestBuilder.get(url.toURI()).setConfig(
                RequestConfig.custom()
                    .setConnectionRequestTimeout(HTTP_GET_TIMOUT_MILLIS)
                    .setConnectTimeout(HTTP_GET_TIMOUT_MILLIS)
                    .setSocketTimeout(HTTP_GET_TIMOUT_MILLIS)
                    .build())

            .setHeader("Accept", "text/html,application/xhtml+xml,application/xml,text/plain,*/*;q=0.5")
            .build();

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response = httpClient.execute(request);
        httpClient.close();

        return response;
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
