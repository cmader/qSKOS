package at.ac.univie.mminf.qskos4j.util.url;

@SuppressWarnings("serial")
class UnhandledHTTPResponseException extends RuntimeException {

	UnhandledHTTPResponseException(int statusCode) {
		super("Unhandled HTTP response code: '" +statusCode+ '"');
	}
	
}
