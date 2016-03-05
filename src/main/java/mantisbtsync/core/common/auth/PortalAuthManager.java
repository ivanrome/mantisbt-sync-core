package mantisbtsync.core.common.auth;

import java.io.IOException;
import java.util.List;

import mantisbtsync.core.common.auth.request.AbstractAuthHttpRequest;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * Manage a connection through http to get an authenfication cookie.
 *
 * @author jdevarulrajah
 *
 */
public class PortalAuthManager {

	/**
	 * HTTP Client.
	 */
	private CloseableHttpClient client = null;

	/**
	 * First Request to execute.
	 */
	private AbstractAuthHttpRequest firstRequest = null;

	/**
	 * Cookie store.
	 */
	private final CookieStore cookieStore = new BasicCookieStore();

	/**
	 * Authenfication cookie.
	 */
	private String authCookie = null;

	/**
	 * @param firstRequest the firstRequest to set
	 */
	public void setFirstRequest(final AbstractAuthHttpRequest firstRequest) {
		this.firstRequest = firstRequest;
	}

	/**
	 * @return the authCookie
	 */
	public String getAuthCookie() {
		return authCookie;
	}

	/**
	 * Default constructor.
	 */
	public PortalAuthManager() {
	}

	/**
	 * EGet the authenfication by executing the defined requests sequence.
	 *
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public void authentificate() throws ClientProtocolException, IOException {
		authCookie = null;

		if (firstRequest != null) {

			cookieStore.clear();
			client = HttpClients.custom().setDefaultCookieStore(cookieStore)
					.setRedirectStrategy(new LaxRedirectStrategy())
					.useSystemProperties().build();

			firstRequest.executeSequence(client);

			final List<Cookie> cookies = cookieStore.getCookies();
			final StringBuilder strBuff = new StringBuilder();
			for (final Cookie cookie : cookies) {
				strBuff.append(cookie.getName());
				strBuff.append("=");
				strBuff.append(cookie.getValue());
				strBuff.append(";");
			}

			authCookie = strBuff.toString();
		}
	}

	/**
	 * Close the http connection.
	 *
	 * @throws IOException
	 */
	public void close() throws IOException {
		authCookie = null;
		if (client != null) {
			client.close();
		}
	}
}
