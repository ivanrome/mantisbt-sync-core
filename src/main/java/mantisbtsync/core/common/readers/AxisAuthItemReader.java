package mantisbtsync.core.common.readers;

import mantisbtsync.core.common.auth.PortalAuthManager;

import org.apache.axis.client.Stub;
import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.batch.item.adapter.ItemReaderAdapter;

/**
 * Read on item from Apache Axis WebService. The WebService method
 * is supposed to return only one item.
 * Can use an authentification cookie if PortalAuthManager is set.
 *
 * @author jdevarulrajah
 *
 */
public class AxisAuthItemReader<T> extends ItemReaderAdapter<T> {

	/**
	 * Auth manager.
	 */
	private PortalAuthManager authManager;

	/**
	 * Client stub generate by Apache Axis.
	 */
	private Stub clientStub;

	/**
	 * @return the authManager
	 */
	public PortalAuthManager getAuthManager() {
		return authManager;
	}

	/**
	 * @param authManager the authManager to set
	 */
	public void setAuthManager(final PortalAuthManager authManager) {
		this.authManager = authManager;
	}

	/**
	 * @return the clientStub
	 */
	public Stub getClientStub() {
		return clientStub;
	}

	/**
	 * @param clientStub the clientStub to set
	 */
	public void setClientStub(final Stub clientStub) {
		this.clientStub = clientStub;
		setTargetObject(clientStub);
	}


	/**
	 * @return return value of the target method.
	 */
	@Override
	public T read() throws Exception {

		// If auth manager is set, try to get the cookie
		if (authManager != null && authManager.getAuthCookie() != null) {
			clientStub._setProperty(HTTPConstants.HEADER_COOKIE,
					authManager.getAuthCookie());
		}

		return super.read();
	}
}
