package mantisbtsync.core.common.auth.request;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Abstract class describing an http request.
 *
 * @author jdevarulrajah
 *
 */
public abstract class AbstractAuthHttpRequest {

	/**
	 * URI of the request.
	 */
	private String uri;

	/**
	 * Next request.
	 */
	private AbstractAuthHttpRequest nextRequest;

	/**
	 * Internal implementation of the request to execute
	 */
	private HttpUriRequest httpRequest;

	/**
	 * Default constructor.
	 */
	public AbstractAuthHttpRequest() {
	}

	/**
	 * Configure the http request by extracting parameters from the previous
	 * response.
	 *
	 * @param entity
	 * 			the previous request
	 * @throws IOException
	 * @throws ParseException
	 */
	public abstract void configFromPreviousResponse(final HttpEntity entity) throws ParseException, IOException;

	/**
	 * Execute the request and all following requests in the sequence.
	 *
	 * @param client
	 * 			HTTP Client
	 * @throws IOException
	 * 			in case of a problem or the connection was aborted
	 * @throws ClientProtocolException
	 * 			in case of an http protocol error
	 */
	public final void executeSequence(final CloseableHttpClient client) throws IOException, ClientProtocolException {
		// TODO : throw exception if initialization is incorrect
		init();
		CloseableHttpResponse response = null;

		try {
			response = client.execute(httpRequest);
			final HttpEntity entity = response.getEntity();

			// TODO: check the status line

			if (nextRequest != null) {
				nextRequest.configFromPreviousResponse(entity);
				EntityUtils.consume(entity);
			}

		} finally {
			// Close the resource before executing the next request
			if (response != null) {
				response.close();
			}
		}

		if (nextRequest != null) {
			nextRequest.executeSequence(client);
		}
	}

	/**
	 * Initialize the request.
	 */
	protected abstract void init();

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(final String uri) {
		this.uri = uri;
	}

	/**
	 * @return the nextRequest
	 */
	public AbstractAuthHttpRequest getNextRequest() {
		return nextRequest;
	}

	/**
	 * @param nextRequest the nextRequest to set
	 */
	public void setNextRequest(final AbstractAuthHttpRequest nextRequest) {
		this.nextRequest = nextRequest;
	}

	/**
	 * @return the httpRequest
	 */
	public HttpUriRequest getHttpRequest() {
		return httpRequest;
	}

	/**
	 * @param httpRequest the httpRequest to set
	 */
	public void setHttpRequest(final HttpUriRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
}

