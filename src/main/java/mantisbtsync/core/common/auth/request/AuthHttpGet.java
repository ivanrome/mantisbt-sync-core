package mantisbtsync.core.common.auth.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;

/**
 * Implementation of a GET request.
 *
 * @author jdevarulrajah
 *
 */
public final class AuthHttpGet extends AbstractAuthHttpRequest {

	/**
	 * Default constructor.
	 */
	public AuthHttpGet() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configFromPreviousResponse(final HttpEntity entity) {
		// No configuration needed from previous response
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		setHttpRequest(new HttpGet(getUri()));
	}
}
