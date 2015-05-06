package de.unistuttgart.iste.rss.bugminer.bugs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraURLParser {
	public static class ParseResult {
		private final String projectKey;
		private final URI baseURI;

		public ParseResult(String projectKey, URI baseURI) {
			this.projectKey = projectKey;
			this.baseURI = baseURI;
		}

		public String getProjectKey() {
			return projectKey;
		}

		public URI getBaseURI() {
			return baseURI;
		}
	}

	private static final String PROJECT_URL_PATTERN = "^(.*)\\/browse\\/([^\\/]*)\\/?$";

	public ParseResult parse(URI uri) {
		Matcher matcher = Pattern.compile(PROJECT_URL_PATTERN).matcher(uri.getPath());
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid project url: must be of form https://server.com/path/browse/KEY");
		}
		String projectKey = matcher.group(2);
		String path = matcher.group(1);
		if (!path.endsWith("/")) {
			path += "/";
		}
		URI baseURI = null;
		try {
			baseURI = new URI(uri.getScheme(), uri.getAuthority(), path, null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid url: " + uri, e);
		}
		return new ParseResult(projectKey, baseURI);
	}
}
