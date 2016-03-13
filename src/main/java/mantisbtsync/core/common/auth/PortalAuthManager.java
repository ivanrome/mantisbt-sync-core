/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jérard Devarulrajah
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
import org.springframework.batch.core.ExitStatus;

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
	public ExitStatus authentificate() throws ClientProtocolException, IOException {
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

		return ExitStatus.COMPLETED;
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
