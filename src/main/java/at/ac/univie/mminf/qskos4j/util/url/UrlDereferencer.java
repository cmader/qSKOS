package at.ac.univie.mminf.qskos4j.util.url;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlDereferencer {

	private final Logger logger = LoggerFactory
			.getLogger(UrlDereferencer.class);
	private final int HTTP_GET_TIMOUT_MILLIS = 60000;

	public String getContentType(final URL url)
			throws UrlNotDereferencableException {
		try {
			final HttpResponse response = sendRequest(url);

			if (isValidResponse(response)) {
				final Header contentType = response
						.getFirstHeader("Content-Type");
				if (contentType == null || contentType.getValue().isEmpty()) {
					throw new NoContentTypeProvidedException();
				}

				return contentType.getValue();
			}
		} catch (final Exception e) {
			// fall through
		}
		throw new UrlNotDereferencableException(url);
	}

	private HttpResponse sendRequest(final URL url) throws URISyntaxException,
			IOException {
		this.logger.debug("dereferencing: " + url.toString());

		final HttpUriRequest request = RequestBuilder
				.get(url.toURI())
				.setConfig(
						RequestConfig
								.custom()
								.setConnectionRequestTimeout(
										this.HTTP_GET_TIMOUT_MILLIS)
								.setConnectTimeout(this.HTTP_GET_TIMOUT_MILLIS)
								.setSocketTimeout(this.HTTP_GET_TIMOUT_MILLIS)
								.setCookieSpec(CookieSpecs.STANDARD).build())

				.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml,text/plain,*/*;q=0.5")
				.build();

		final CloseableHttpClient httpClient = HttpClientBuilder.create()
				.build();
		final HttpResponse response = httpClient.execute(request);
		httpClient.close();

		return response;
	}

	private boolean isValidResponse(final HttpResponse response) {
		final int statusCode = response.getStatusLine().getStatusCode();
		this.logger.debug("response status: " + statusCode);

		if (Integer.toString(statusCode).startsWith("4")
				|| Integer.toString(statusCode).startsWith("5")) {
			return false;
		} else {
			switch (statusCode) {
			case 200:
				return true;
			default:
				throw new UnhandledHTTPResponseException(statusCode);
			}
		}
	}

}
