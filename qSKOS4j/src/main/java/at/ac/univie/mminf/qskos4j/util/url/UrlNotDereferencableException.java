package at.ac.univie.mminf.qskos4j.util.url;

import java.net.URL;

@SuppressWarnings("serial")
public class UrlNotDereferencableException extends Exception {

	public UrlNotDereferencableException(URL url) {
		super("Unable to dereference URL '"+url+"'");
	}
	
}
